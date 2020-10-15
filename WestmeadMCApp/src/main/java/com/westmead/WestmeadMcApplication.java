package com.westmead;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.westmead.document.AvailableTime;
import com.westmead.document.Doctor;

@SpringBootApplication
public class WestmeadMcApplication {

	public static void main(String[] args) {
		SpringApplication.run(WestmeadMcApplication.class, args);
	}

}
