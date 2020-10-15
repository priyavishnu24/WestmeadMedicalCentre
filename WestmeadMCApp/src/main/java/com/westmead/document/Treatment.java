package com.westmead.document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Treatment {

	private String comment;
	private Map<String, Float> bill;
	private Set<String> docs;
	
	public Treatment() {
		this.bill = new HashMap<String, Float>();
		this.docs = new HashSet<String>();
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Map<String, Float> getBill() {
		return bill;
	}
	public void setBill(Map<String, Float> bill) {
		this.bill = bill;
	}
	
	public Set<String> getDocs() {
		return docs;
	}
	public void setDocs(Set<String> docs) {
		this.docs = docs;
	}
	@Override
	public String toString() {
		return "Treatment [comment=" + comment + ", bill=" + bill + ", docs=" + docs + "]";
	}
	
	
}
