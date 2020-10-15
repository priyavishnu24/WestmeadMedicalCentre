package com.westmead.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.westmead.document.Admin;
import com.westmead.document.Appointment;
import com.westmead.document.AvailableTime;
import com.westmead.document.Doctor;
import com.westmead.document.Patient;
import com.westmead.document.Treatment;
import com.westmead.dto.AppointmentDTO;
import com.westmead.exception.FileStorageException;
import com.westmead.exception.LoginFailureException;
import com.westmead.exception.RegistrationFailureException;
import com.westmead.notification.EmailNotification;
import com.westmead.notification.SMSNotification;
import com.westmead.repository.AppointmentRepository;
import com.westmead.repository.DoctorRepository;
import com.westmead.repository.PatientRepository;

@Service
@PropertySource("classpath:notification.properties")
public class DoctorService {

	private static Logger logger = LoggerFactory.getLogger(DoctorService.class);

	private static final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final Path fileStorageLocation;

	private static final Path treatmentDocStorageLocation;

	private static final ObjectMapper mapper;

	static {
		logger.info("Upload dir path : " + System.getenv("CATALINA_HOME") + "/webapps/ROOT/assets/images/doctors");
		fileStorageLocation = Paths.get(System.getenv("CATALINA_HOME") + "/webapps/ROOT/assets/images/doctors")
				.toAbsolutePath().normalize();

		treatmentDocStorageLocation = Paths
				.get(System.getenv("CATALINA_HOME") + "/webapps/Root/assets/images/treatmentDocs").toAbsolutePath()
				.normalize();
		treatmentDocStorageLocation.toFile().mkdir();

		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	@Autowired
	private DoctorRepository doctorRepo;

	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private PatientRepository patientRepo;

	@Autowired
	private Environment env;

	public String saveDoctor(Doctor doctor) throws RegistrationFailureException {
		doctor.setEmailId(doctor.getEmailId().toLowerCase());
		Optional<Doctor> optionalUser = this.doctorRepo.findById(doctor.getEmailId());
		if (!optionalUser.isPresent()) {
			this.doctorRepo.save(doctor);

			// E-mail Notification
			String subject = env.getProperty("doctor.register.email.subject");
			String content = env.getProperty("doctor.register.email.body").replace("<DOCTOR_NAME>",
					doctor.getLastName());
			new Thread(new EmailNotification(doctor.getEmailId(), subject, content)).start();
			// SMS Notification
			new Thread(new SMSNotification(doctor.getPhoneNo(), content)).start();

			return "Success";
		} else {
			logger.info("E-mail id already registered");
			throw new RegistrationFailureException("E-mail id already registered");
		}
	}

	public Doctor validateDoctor(String emailId, String password) throws LoginFailureException {
		logger.debug(emailId);
		logger.debug(password);
		Optional<Doctor> optionalUser = this.doctorRepo.findById(emailId.toLowerCase());
		if (optionalUser.isPresent()) {
			Doctor doctor = optionalUser.get();
			if (doctor.getApproval().getStatus() == null) {
				logger.info("Please wait for admin to approve");
				throw new LoginFailureException("Please wait for admin to approve");
			} else if (doctor.getApproval().getStatus().equals("rejected")) {
				logger.info("Contact Admin!! your approval has been rejected");
				throw new LoginFailureException("Contact Admin!! your approval has been rejected");
			} else if (doctor.getApproval().getStatus().equals("approved") && doctor.getPassword().equals(password)) {
				// doctor.setPassword("");
				doctor.getAvailableTimes().entrySet().parallelStream().forEach(entry -> {
					if (entry.getKey().isBefore(LocalDate.now())) {
						doctor.getAvailableTimes().remove(entry.getKey());
					}

				});
				return doctor;
			} else {
				logger.info("Password mismatch");
				throw new LoginFailureException("Password mismatch");
			}
		} else {
			logger.info("Email id is not registered");
			throw new LoginFailureException("Email id is not registered");
		}

	}

	public List<Doctor> getNonApprovedDoctors() {
		return this.doctorRepo.findNonApprovedDoctors();
	}

	public boolean approveDoctor(Doctor doctor) {
		Optional<Doctor> optionalUser = this.doctorRepo.findById(doctor.getEmailId().toLowerCase());
		if (optionalUser.isPresent()) {
			Doctor doctorObj = optionalUser.get();
			doctorObj.setApproval(doctor.getApproval());
			this.doctorRepo.save(doctorObj);

			// E-mail Notification
			String subject = env.getProperty("doctor.approved.email.subject");
			String content = env.getProperty("doctor.approved.email.body").replace("<DOCTOR_NAME>",
					doctorObj.getLastName());
			new Thread(new EmailNotification(doctorObj.getEmailId(), subject, content)).start();
			// SMS Notification
			new Thread(new SMSNotification(doctorObj.getPhoneNo(), content)).start();

			return true;
		} else {
			return false;
		}
	}

	public List<Doctor> getApprovedDoctors() {
		List<Doctor> doctors = this.doctorRepo.findApprovedDoctors();
		doctors.parallelStream().forEach(doctor -> {
			doctor.getAvailableTimes().entrySet().parallelStream().forEach(entry -> {
				if (entry.getKey().isBefore(LocalDate.now())) {
					doctor.getAvailableTimes().remove(entry.getKey());
				}

			});
		});
		return doctors;
	}

	public Doctor getDoctor(String emailId) throws NoSuchElementException {
		return this.doctorRepo.findById(emailId.toLowerCase()).get();
	}

	/**
	 * This will be called when booking/cancel the appointment
	 * 
	 * @param doctor
	 * @return
	 */
	public Doctor updateDoctor(Doctor doctor) {
		return this.doctorRepo.save(doctor);
	}

	public Doctor addAvailableTime(String doctorId, String date, List<String> times) throws RuntimeException {
		logger.info("Doctor Id = " + doctorId);
		logger.info("Date = " + date);
		logger.info("Times = " + times);
		Doctor doctor = this.doctorRepo.findById(doctorId).get();
		LocalDate localDate = LocalDate.parse(date, DATEFORMATTER);

		Map<LocalDate, List<AvailableTime>> availableTimesMap = doctor.getAvailableTimes();
		List<AvailableTime> availableTimeList = availableTimesMap.get(localDate);
		if (availableTimeList == null) {
			availableTimeList = new ArrayList<AvailableTime>();
			availableTimesMap.put(localDate, availableTimeList);
		}
		List<AvailableTime> availableTimes = (List<AvailableTime>) ((ArrayList) availableTimeList).clone();
		List<AvailableTime> newAvailableTimes = (List<AvailableTime>) ((ArrayList) availableTimeList).clone();

		times.parallelStream().forEach(timeStr -> {
			LocalTime localTime = LocalTime.parse(timeStr);
			if (!availableTimes.parallelStream().anyMatch(availableTimeObj -> {
				return availableTimeObj.getTime().compareTo(localTime) == 0;
			})) {
				AvailableTime availableTime = new AvailableTime();
				availableTime.setTime(localTime);
				newAvailableTimes.add(availableTime);
			} else {
				throw new RuntimeException(localTime.toString() + " is already added.");
			}
		});

		availableTimesMap.put(localDate, newAvailableTimes);
		doctor = this.doctorRepo.save(doctor);
		// doctor.setPassword("");

		return doctor;
	}

	public Doctor removeAvailableTime(String doctorId, String date, List<String> times) throws RuntimeException {
		logger.info("Doctor Id = " + doctorId);
		logger.info("Date = " + date);
		logger.info("Times = " + times);
		Doctor doctor = this.doctorRepo.findById(doctorId).get();
		LocalDate localDate = LocalDate.parse(date, DATEFORMATTER);

		Map<LocalDate, List<AvailableTime>> availableTimesMap = doctor.getAvailableTimes();
		if (availableTimesMap == null) {
			throw new RuntimeException("The doctor has no available time");
		}
		List<AvailableTime> availableTimeList = availableTimesMap.get(localDate);
		if (availableTimeList == null) {
			throw new RuntimeException("The doctor has no available time on " + date);
		}

		List<AvailableTime> availableTimes = availableTimeList;

		times.parallelStream().forEach(timeStr -> {
			LocalTime localTime = LocalTime.parse(timeStr);
			if (availableTimes.parallelStream().anyMatch(availableTimeObj -> {
				return availableTimeObj.getTime().compareTo(localTime) == 0;
			})) {
				List<AvailableTime> filteredAvailableTime = availableTimes.parallelStream().filter(availableTimeObj -> {
					return availableTimeObj.getTime().compareTo(localTime) == 0;
				}).collect(Collectors.toList());
				if (filteredAvailableTime.size() == 1) {
					availableTimeList.remove(filteredAvailableTime.get(0));
				} else {
					logger.error("Filtered List has more times " + filteredAvailableTime);
				}

			} else {
				throw new RuntimeException(localTime.toString() + " is not added already.");
			}
		});

		doctor = this.doctorRepo.save(doctor);
		// doctor.setPassword("");

		return doctor;
	}

	public void updateDoctor(MultipartFile file, String doctorStr)
			throws FileStorageException, JsonMappingException, JsonProcessingException {
		Doctor doctor = mapper.readValue(doctorStr, Doctor.class);
		logger.info("Doctor : " + doctor);
		Doctor doctorDB = this.doctorRepo.findById(doctor.getEmailId().toLowerCase()).get();
		if (!file.isEmpty()) {
			// Normalize file name
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			fileName = doctor.getEmailId() + fileName.substring(fileName.lastIndexOf("."));
			logger.info("Uploading file : " + fileName);
			try {
				// Check if the file's name contains invalid characters
				if (fileName.contains("..")) {
					throw new FileStorageException(
							"Sorry! Filename contains invalid path sequence " + doctor.getEmailId());
				}

				// Copy file to the target location (Replacing existing file with the same name)
				Path targetLocation = fileStorageLocation.resolve(fileName);
				Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			} catch (IOException ex) {
				throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
			}

			doctorDB.setImageURL("assets/images/doctors/" + fileName);
		}

		doctorDB.setAge(doctor.getAge());
		doctorDB.setExperience(doctor.getExperience());
		doctorDB.setFirstName(doctor.getFirstName());
		doctorDB.setGender(doctor.getGender());
		doctorDB.setLastName(doctor.getLastName());
		doctorDB.setPassword(doctor.getPassword());
		doctorDB.setPhoneNo(doctor.getPhoneNo());
		doctorDB.setQualification(doctor.getQualification());

		this.doctorRepo.save(doctorDB);

		// E-mail Notification
		String subject = env.getProperty("doctor.updated.email.subject");
		String content = env.getProperty("doctor.updated.email.body").replace("<DOCTOR_NAME>", doctorDB.getLastName());
		new Thread(new EmailNotification(doctorDB.getEmailId(), subject, content)).start();
		// SMS Notification
		new Thread(new SMSNotification(doctorDB.getPhoneNo(), content)).start();
	}

	public List<AppointmentDTO> getAppointments(String doctorId, String date) {
		List<AppointmentDTO> appointments = new ArrayList<AppointmentDTO>();
		Appointment appointment = new Appointment();
		appointment.setDoctorId(doctorId);
		appointment.setAppointmentDate(LocalDate.parse(date));
		List<Appointment> appointmentDocs = this.appointmentRepo.findAll(Example.of(appointment));
		logger.info(appointmentDocs.toString());

		appointmentDocs.parallelStream().forEach(appointmentDoc -> {
			Optional<Patient> optionalPatient = this.patientRepo.findById(appointmentDoc.getPatientId());
			if (optionalPatient.isPresent()) {
				Patient patient = optionalPatient.get();
				patient.setAppointmentIds(null);
				patient.setPassword(null);
				AppointmentDTO dto = new AppointmentDTO();
				dto.setAppointmentId(appointmentDoc.getAppointmentId());
				dto.setAppointmentTime(
						LocalDateTime.of(appointmentDoc.getAppointmentDate(), appointmentDoc.getAppointmentTime()));
				dto.setBookedBy(appointmentDoc.getBookedBy());
				dto.setPatient(patient);
				dto.setReason(appointmentDoc.getReason());
				dto.setTreatment(appointmentDoc.getTreatment());

				appointments.add(dto);
			}
		});

		return appointments;
	}

	public void saveTreatment(MultipartFile[] files, String comments, String appointmentId) throws IOException {
		File folder = new File(treatmentDocStorageLocation.toFile(), appointmentId);
		folder.mkdir();
		Path path = folder.toPath();
		Set<String> docNames = new HashSet<String>();
		for (MultipartFile file : files) {
			// Copy file to the target location (Replacing existing file with the same name)
			String fileName = StringUtils.cleanPath(file.getOriginalFilename());
			Path targetLocation = path.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			docNames.add(fileName);
		}

		Optional<Appointment> optionalAppointment = this.appointmentRepo.findById(appointmentId);
		if (optionalAppointment.isPresent()) {
			Treatment treatment = new Treatment();
			treatment.setComment(comments);
			treatment.setDocs(docNames);

			Appointment appointment = optionalAppointment.get();
			appointment.setTreatment(treatment);

			this.appointmentRepo.save(appointment);

			// E-mail Notification
			String subject = env.getProperty("patient.treatment.save.email.subject")
					.replace("<DATE>", appointment.getAppointmentDate().toString())
					.replace("<TIME>", appointment.getAppointmentTime().toString());
			String content = env.getProperty("patient.treatment.save.email.body").replace("<CUSTOMER_NAME>",
					"Customer");
			new Thread(new EmailNotification(appointment.getPatientId(), subject, content)).start();
			// SMS Notification
			Patient patient = this.patientRepo.findById(appointment.getPatientId()).get();
			new Thread(new SMSNotification(patient.getPhoneNo(), content)).start();
		}

	}
}
