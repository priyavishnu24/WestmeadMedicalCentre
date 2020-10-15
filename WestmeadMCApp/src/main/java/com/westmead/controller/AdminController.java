package com.westmead.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.westmead.document.Admin;
import com.westmead.exception.BookingAppointmentFailureException;
import com.westmead.exception.LoginFailureException;
import com.westmead.exception.RegistrationFailureException;
import com.westmead.service.AdminService;
import com.westmead.service.PatientService;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	
	@Autowired
	private PatientService patientService;
	
	@PostMapping("/register")
	public String registerUser(@RequestBody Admin admin) throws RegistrationFailureException {
		return this.adminService.saveAdmin(admin);
	}
	
	@PostMapping("/login")
	public Admin validateAdminUser(@RequestParam String emailId, @RequestParam String password) throws LoginFailureException {
		return this.adminService.validateAdmin(emailId, password);
	}
	
	@GetMapping("/getNonApproved")
	public List<Admin> getNonApprovedAdmins(){
		return this.adminService.getNonApprovedAdmins();
	}
	
	@PostMapping("/approve")
	public boolean approveAdmin(@RequestBody Admin admin){
		return this.adminService.approveAdmin(admin);
	}
	
	@PostMapping("/update")
	public void updateAdmin(@RequestBody Admin admin){
		this.adminService.updateAdmin(admin);
	}
	
	@PostMapping("/bookAppointment")
	public boolean bookAppointment(@RequestParam String patientId, @RequestParam String doctorId,
			@RequestParam String date, @RequestParam String time, @RequestParam String reason, @RequestParam String adminId) throws BookingAppointmentFailureException {
		return this.patientService.bookAppointment(patientId, doctorId, LocalDate.parse(date), LocalTime.parse(time), reason, adminId);
	}
	
	@GetMapping
	public String test() {
		return "Admin service working";
	}
}
