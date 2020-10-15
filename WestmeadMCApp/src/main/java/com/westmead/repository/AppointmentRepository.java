package com.westmead.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.westmead.document.Appointment;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String>{

}
