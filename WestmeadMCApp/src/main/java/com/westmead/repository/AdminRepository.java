package com.westmead.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.westmead.document.Admin;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String>{

	@Query("{'approval.status' : null}")
	public List<Admin> findNonApprovedAdmins();
}
