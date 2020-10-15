package com.westmead.document;

import java.util.Calendar;

public class Approval {

	private String status;
	private String approvedBy;
	private Calendar approvedDate;
	

	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	public Calendar getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(Calendar approvedDate) {
		this.approvedDate = approvedDate;
	}
	@Override
	public String toString() {
		return "Approval [status=" + status + ", approvedBy=" + approvedBy + ", approvedDate=" + approvedDate + "]";
	}
	
	
	
}
