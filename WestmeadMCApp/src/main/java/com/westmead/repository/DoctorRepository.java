package com.westmead.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.westmead.document.Doctor;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String>{

	@Query("{'approval.status' : null}")
	public List<Doctor> findNonApprovedDoctors();
	
	@Query("{'approval.status' : 'approved'}")
	public List<Doctor> findApprovedDoctors();
}
