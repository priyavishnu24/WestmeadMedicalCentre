package com.westmead.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.westmead.document.Patient;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String>{

}
