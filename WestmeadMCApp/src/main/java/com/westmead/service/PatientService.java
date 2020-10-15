package com.westmead.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.westmead.document.Appointment;
import com.westmead.document.Doctor;
import com.westmead.document.Patient;
import com.westmead.dto.AppointmentDTO;
import com.westmead.exception.BookingAppointmentFailureException;
import com.westmead.exception.LoginFailureException;
import com.westmead.exception.RegistrationFailureException;
import com.westmead.notification.EmailNotification;
import com.westmead.notification.SMSNotification;
import com.westmead.repository.AppointmentRepository;
import com.westmead.repository.PatientRepository;

@Service
@PropertySource("classpath:notification.properties")
public class PatientService {

	private static Logger logger = LoggerFactory.getLogger(PatientService.class);

	@Autowired
	private PatientRepository patientRepo;

	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private DoctorService doctorService;

	@Autowired
	private Environment env;

	public String savePatient(Patient user) throws RegistrationFailureException {
		user.setEmailId(user.getEmailId().toLowerCase());
		Optional<Patient> optionalUser = this.patientRepo.findById(user.getEmailId());
		if (!optionalUser.isPresent()) {
			this.patientRepo.save(user);

			// E-mail Notification
			String subject = env.getProperty("patient.register.email.subject");
			String content = env.getProperty("patient.register.email.body").replace("<CUSTOMER_NAME>",
					user.getLastName());
			new Thread(new EmailNotification(user.getEmailId(), subject, content)).start();
			// SMS Notification
			new Thread(new SMSNotification(user.getPhoneNo(), content)).start();
			
			return "Success";
		} else {
			logger.info("E-mail id already registered");
			throw new RegistrationFailureException("E-mail id already registered");
		}
	}

	public Patient validatePatient(String emailId, String password) throws LoginFailureException {
		System.out.println(emailId);
		System.out.println(password);
		Optional<Patient> optionalUser = this.patientRepo.findById(emailId.toLowerCase());
		if (optionalUser.isPresent()) {
			Patient user = optionalUser.get();
			if (user.getPassword().equals(password)) {
				// user.setPassword("");
				return user;
			} else {
				logger.info("Password mismatch");
				throw new LoginFailureException("Password mismatch");
			}
		} else {
			logger.info("Email id is not registered");
			throw new LoginFailureException("Email id is not registered");
		}
	}

	public boolean bookAppointment(String patientId, String doctorId, LocalDate date, LocalTime time, String reason,
			String adminId) throws BookingAppointmentFailureException {
		Doctor doctor = null;
		try {
			doctor = this.doctorService.getDoctor(doctorId);
		} catch (NoSuchElementException e) {
			throw new BookingAppointmentFailureException("Invalid Doctor Id");
		}

		Optional<Patient> optionalPatient = this.patientRepo.findById(patientId);
		if (!optionalPatient.isPresent()) {
			throw new BookingAppointmentFailureException("Invalid patient Id");
		}
		Appointment appointment = new Appointment();
		appointment.setDoctorId(doctorId);
		appointment.setPatientId(patientId);
		appointment.setReason(reason);
		if (adminId == null)
			appointment.setBookedBy("self");
		else
			appointment.setBookedBy(adminId);
		appointment.setAppointmentDate(date);
		appointment.setAppointmentTime(time);
		appointment.setAppointmentId(UUID.randomUUID().toString());

		Patient patient = optionalPatient.get();
		patient.getAppointmentIds().add(appointment.getAppointmentId());

		doctor.getAvailableTimes().get(date).parallelStream().forEach(availableTime -> {
			if (availableTime.getTime().compareTo(time) == 0) {
				if (availableTime.getAppointmentId() != null)
					throw new RuntimeException("The selected time is not available");
				availableTime.setAppointmentId(appointment.getAppointmentId());
			}
		});
		logger.info("New Appointment : " + appointment);
		try {
			this.appointmentRepo.save(appointment);
			this.patientRepo.save(patient);
			this.doctorService.updateDoctor(doctor);

			// E-mail Notification
			String subject = env.getProperty("patient.book.appointment.email.subject")
					.replace("<DATE>", appointment.getAppointmentDate().toString())
					.replace("<TIME>", appointment.getAppointmentTime().toString());
			String content = env.getProperty("patient.book.appointment.email.body")
					.replace("<CUSTOMER_NAME>", patient.getLastName()).replace("<DOCTOR_NAME>", doctor.getLastName())
					.replace("<DATE>", appointment.getAppointmentDate().toString())
					.replace("<TIME>", appointment.getAppointmentTime().toString())
					.replace("<REASON>", appointment.getReason());
			new Thread(new EmailNotification(patient.getEmailId(), subject, content)).start();
			// SMS Notification
			new Thread(new SMSNotification(patient.getPhoneNo(), content)).start();

			return true;
		} catch (Exception e) {
			logger.error("Error during appointment booking", e);
			throw new BookingAppointmentFailureException("Booking Failed");
		}

	}

	public void updatePatient(Patient patient) {
		Patient patientDB = this.patientRepo.findById(patient.getEmailId().toLowerCase()).get();
		patientDB.setAge(patient.getAge());
		patientDB.setFirstName(patient.getFirstName());
		patientDB.setGender(patient.getGender());
		patientDB.setLastName(patient.getLastName());
		patientDB.setPassword(patient.getPassword());
		patientDB.setPhoneNo(patient.getPhoneNo());

		this.patientRepo.save(patientDB);
		
		// E-mail Notification
		String subject = env.getProperty("patient.updated.email.subject");
		String content = env.getProperty("patient.updated.email.body")
				.replace("<CUSTOMER_NAME>",  patient.getLastName());
		new Thread(new EmailNotification(patient.getEmailId(), subject, content)).start();
		// SMS Notification
		new Thread(new SMSNotification(patient.getPhoneNo(), content)).start();
	}

	public List<AppointmentDTO> getAppointments(String patientId) {
		List<AppointmentDTO> appointments = new ArrayList<AppointmentDTO>();
		Appointment appointment = new Appointment();
		appointment.setPatientId(patientId);
		List<Appointment> appointmentDocs = this.appointmentRepo.findAll(Example.of(appointment));
		logger.info(appointmentDocs.toString());

		appointmentDocs.parallelStream().forEach(appointmentDoc -> {
			Doctor doctor = this.doctorService.getDoctor(appointmentDoc.getDoctorId());
			doctor.setAvailableTimes(null);
			doctor.setPassword(null);

			AppointmentDTO dto = new AppointmentDTO();
			dto.setAppointmentId(appointmentDoc.getAppointmentId());
			dto.setAppointmentTime(
					LocalDateTime.of(appointmentDoc.getAppointmentDate(), appointmentDoc.getAppointmentTime()));
			dto.setBookedBy(appointmentDoc.getBookedBy());
			dto.setDoctor(doctor);
			dto.setReason(appointmentDoc.getReason());
			dto.setTreatment(appointmentDoc.getTreatment());

			appointments.add(dto);
		});

		return appointments;
	}

	public boolean cancelAppointment(String appointmentId) {
		Optional<Appointment> appointmentOptional = this.appointmentRepo.findById(appointmentId);
		
		if (appointmentOptional.isPresent()) {
			Appointment appointment = appointmentOptional.get();
			Patient patient = null;
			if (appointment.getTreatment() == null) {
				Optional<Patient> patientOptional = this.patientRepo.findById(appointment.getPatientId());
				if (patientOptional.isPresent()) {
					patient = patientOptional.get();
					patient.getAppointmentIds().remove(appointmentId);
					
					this.patientRepo.save(patient);
				}

				Doctor doctor = this.doctorService.getDoctor(appointment.getDoctorId());
				doctor.getAvailableTimes().get(appointment.getAppointmentDate()).parallelStream().forEach(at -> {
					if (at.getAppointmentId() != null && appointmentId.equals(at.getAppointmentId())) {
						at.setAppointmentId(null);
					}
				});

				this.doctorService.updateDoctor(doctor);

				this.appointmentRepo.deleteById(appointmentId);

				// E-mail Notification
				String subject = env.getProperty("patient.cancel.appointment.email.subject")
						.replace("<DATE>", appointment.getAppointmentDate().toString())
						.replace("<TIME>", appointment.getAppointmentTime().toString());
				String content = env.getProperty("patient.cancel.appointment.email.body")
						.replace("<CUSTOMER_NAME>", patient.getLastName()).replace("<DOCTOR_NAME>", doctor.getLastName())
						.replace("<DATE>", appointment.getAppointmentDate().toString())
						.replace("<TIME>", appointment.getAppointmentTime().toString())
						.replace("<REASON>", appointment.getReason());
				new Thread(new EmailNotification(patient.getEmailId(), subject, content)).start();
				// SMS Notification
				new Thread(new SMSNotification(patient.getPhoneNo(), content)).start();
				
				return true;
			} else {
				throw new RuntimeException("Treatment completed for this appointment");
			}
		} else {
			return false;
		}
	}

	public String test(String propertyName) {
		logger.info(env.getProperty(propertyName));
		EmailNotification email = new EmailNotification("t.isaacjefferson@gmail.com", env.getProperty("patient.register.email.subject"),
				env.getProperty("patient.register.email.body"));
		new Thread(email).start();
		return env.getProperty(propertyName);
	}

}
