package com.westmead.exception;

public class LoginFailureException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3418613072090161598L;

	public LoginFailureException(String message) {
		super(message);
	}
}
