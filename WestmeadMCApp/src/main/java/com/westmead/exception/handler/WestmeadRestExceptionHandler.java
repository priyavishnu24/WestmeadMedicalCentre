package com.westmead.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.westmead.exception.BookingAppointmentFailureException;
import com.westmead.exception.FileStorageException;
import com.westmead.exception.LoginFailureException;
import com.westmead.exception.RegistrationFailureException;

@RestControllerAdvice
public class WestmeadRestExceptionHandler {

	private static Logger logger = LoggerFactory.getLogger(WestmeadRestExceptionHandler.class);
			
	@ExceptionHandler(LoginFailureException.class)
	public ResponseEntity<String> handleLoginFailureException(LoginFailureException exception) {
		logger.error(exception.getMessage());
		return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(RegistrationFailureException.class)
	public ResponseEntity<String> handleRegistrationFailureException(RegistrationFailureException exception) {
		logger.error(exception.getMessage());
		return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(BookingAppointmentFailureException.class)
	public ResponseEntity<String> handleBookingAppointmentFailureException(BookingAppointmentFailureException exception) {
		logger.error(exception.getMessage());
		return new ResponseEntity<String>(exception.getMessage(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(FileStorageException.class)
	public ResponseEntity<String> handleFileStorageException(FileStorageException exception) {
		logger.error(exception.getMessage());
		return new ResponseEntity<String>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		
	}
	
	@ExceptionHandler(Throwable.class)
	public ResponseEntity<String> handleThrowable(Throwable exception) {
		logger.error(exception.getMessage(), exception);
		return new ResponseEntity<String>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	
}
