package com.westmead.document;

import java.time.LocalTime;

public class AvailableTime {

	private LocalTime time;
	private String appointmentId;
	
	public LocalTime getTime() {
		return time;
	}
	public void setTime(LocalTime time) {
		this.time = time;
	}
	public String getAppointmentId() {
		return appointmentId;
	}
	public void setAppointmentId(String appointmentId) {
		this.appointmentId = appointmentId;
	}
	@Override
	public String toString() {
		return "AvailableTime [time=" + time + ", appointmentId=" + appointmentId + "]";
	}
	
	
}
