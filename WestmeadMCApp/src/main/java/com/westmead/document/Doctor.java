package com.westmead.document;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.key.LocalDateKeyDeserializer;

@Document
public class Doctor {

	private String firstName;
	private String lastName;
	private String gender;
	private int age;
	@Id
	private String emailId;
	private long phoneNo;
	private String password;
	private String qualification;
	private int experience;
	private Approval approval;
	private String imageURL;
	private Map<LocalDate, List<AvailableTime>> availableTimes;
	
	public Doctor() {
		this.approval = new Approval();
		this.availableTimes = new ConcurrentHashMap<LocalDate, List<AvailableTime>>();
		this.imageURL = "assets/images/doctors/default_doctor.jpg";
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
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public Approval getApproval() {
		return approval;
	}
	public void setApproval(Approval approval) {
		this.approval = approval;
	}
	public Map<LocalDate, List<AvailableTime>> getAvailableTimes() {
		return availableTimes;
	}
	public void setAvailableTimes(Map<LocalDate, List<AvailableTime>> availableTimes) {
		this.availableTimes = availableTimes;
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
	
	public int getExperience() {
		return experience;
	}
	public void setExperience(int experience) {
		this.experience = experience;
	}
	
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	@Override
	public String toString() {
		return "Doctor [firstName=" + firstName + ", lastName=" + lastName + ", gender=" + gender + ", age=" + age
				+ ", emailId=" + emailId + ", phoneNo=" + phoneNo + ", password=" + password + ", qualification="
				+ qualification + ", experience=" + experience + ", approval=" + approval + ", imageURL=" + imageURL
				+ ", availableTimes=" + availableTimes + "]";
	}
	
	
}
