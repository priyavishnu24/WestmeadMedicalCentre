package com.westmead.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.westmead.document.Doctor;
import com.westmead.dto.AppointmentDTO;
import com.westmead.exception.FileStorageException;
import com.westmead.exception.LoginFailureException;
import com.westmead.exception.RegistrationFailureException;
import com.westmead.service.DoctorService;

@CrossOrigin
@RestController
@RequestMapping("/doctor")
public class DoctorController {

	@Autowired
	private DoctorService doctorService;
	
	@PostMapping("/register")
	public String registerUser(@RequestBody Doctor user) throws RegistrationFailureException {
		return this.doctorService.saveDoctor(user);
	}
	
	@PostMapping("/login")
	public Doctor validateAdminUser(@RequestParam String emailId, @RequestParam String password) throws LoginFailureException {
		return this.doctorService.validateDoctor(emailId, password);
	}
	
	@GetMapping("/getNonApproved")
	public List<Doctor> getNonApprovedDoctors(){
		return this.doctorService.getNonApprovedDoctors();
	}
	
	@PostMapping("/approve")
	public boolean approveDoctor(@RequestBody Doctor doctor){
		return this.doctorService.approveDoctor(doctor);
	}
	
	@GetMapping("/getApprovedDoctors")
	public List<Doctor> getApprovedDoctors(){
		return this.doctorService.getApprovedDoctors();
	}
	
	@PostMapping("/addAvailableTime")
	public Doctor addAvailableTime(@RequestParam String doctorId, @RequestParam String date, @RequestParam List<String> times) {
		return this.doctorService.addAvailableTime(doctorId, date, times);
	}
	
	@PostMapping("/removeAvailableTime")
	public Doctor removeAvailableTime(@RequestParam String doctorId, @RequestParam String date, @RequestParam List<String> times) {
		return this.doctorService.removeAvailableTime(doctorId, date, times);
	}
	
	@GetMapping("/getDoctor")
	public Doctor getDoctor(@RequestParam String doctorId) {
		return this.doctorService.getDoctor(doctorId);
	}
	
	@PostMapping("/uploadDoctor")
	 public void uploadDoctor(@RequestParam MultipartFile file, @RequestParam String doctor) throws FileStorageException, JsonMappingException, JsonProcessingException {
		this.doctorService.updateDoctor(file, doctor);
	 }
	
	@GetMapping("/getAppointments")
	public List<AppointmentDTO> getAppointments(@RequestParam String doctorId, @RequestParam String date) {
		return this.doctorService.getAppointments(doctorId, date);
	}
	
	@PostMapping("/saveTreatment")
	public void saveTreatment(@RequestParam MultipartFile[] files, @RequestParam String comments, @RequestParam String appointmentId) throws IOException {
		this.doctorService.saveTreatment(files, comments, appointmentId);
	}
	
	@GetMapping
	public String test() {
		return "Doctor service working";
	}
}
