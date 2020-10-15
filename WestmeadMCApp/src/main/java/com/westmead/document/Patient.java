package com.westmead.document;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Patient {
	
	
	private String firstName;
	private String lastName;
	private String gender;
	private int age;
	@Id private String emailId;
	private long phoneNo;
	private String password;
	private Set<String> appointmentIds;
	
	public Patient() {
		this.appointmentIds = new HashSet<String>();
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public long getPhoneNo() {
		return phoneNo;
	}
	public void setPhoneNo(long phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public Set<String> getAppointmentIds() {
		return appointmentIds;
	}
	public void setAppointmentIds(Set<String> appointmentIds) {
		this.appointmentIds = appointmentIds;
	}
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	@Override
	public String toString() {
		return "Patient [firstName=" + firstName + ", lastName=" + lastName + ", gender=" + gender + ", age=" + age
				+ ", emailId=" + emailId + ", phoneNo=" + phoneNo + ", password=" + password + ", appointmentIds="
				+ appointmentIds + "]";
	}

	
	
}
