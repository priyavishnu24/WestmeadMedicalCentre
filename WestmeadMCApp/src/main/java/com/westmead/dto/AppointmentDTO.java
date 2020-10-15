package com.westmead.dto;

import java.time.LocalDateTime;

import com.westmead.document.Doctor;
import com.westmead.document.Patient;
import com.westmead.document.Treatment;

public class AppointmentDTO {

	private String appointmentId;
	private LocalDateTime appointmentTime;
	private Patient patient;
	private Doctor doctor;
	private String reason;
	private String bookedBy;
	private Treatment treatment;
	
	public String getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}
	public LocalDateTime getAppointmentTime() {
		return appointmentTime;
	}
	public void setAppointmentTime(LocalDateTime appointmentTime) {
		this.appointmentTime = appointmentTime;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getBookedBy() {
		return bookedBy;
	}
	public void setBookedBy(String bookedBy) {
		this.bookedBy = bookedBy;
	}
	public Treatment getTreatment() {
		return treatment;
	}
	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}
	
	public Patient getPatient() {
		return patient;
	}
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	@Override
	public String toString() {
		return "AppointmentDTO [appointmentId=" + appointmentId + ", appointmentTime=" + appointmentTime + ", patient="
				+ patient + ", doctor=" + doctor + ", reason=" + reason + ", bookedBy=" + bookedBy + ", treatment="
				+ treatment + "]";
	}
	
	
	
	
}
