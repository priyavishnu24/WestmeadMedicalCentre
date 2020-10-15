package com.westmead.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Admin {

	private String firstName;
	private String lastName;
	@Id
	private String emailId;
	private long phoneNo;
	private String password;
	private Approval approval;
	
	public Admin() {
		this.approval = new Approval();
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
	public Approval getApproval() {
		return approval;
	}
	public void setApproval(Approval approval) {
		this.approval = approval;
	}
	@Override
	public String toString() {
		return "Admin [firstName=" + firstName + ", lastName=" + lastName + ", emailId=" + emailId + ", phoneNo="
				+ phoneNo + ", password=" + password + ", approval=" + approval + "]";
	}
	
	
}
