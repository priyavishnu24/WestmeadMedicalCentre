package com.westmead.notification;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;


public class SMSNotification implements Runnable {
	
	  public static final String ACCOUNT_SID = "AC4e6959776b2277b3aaf79095739418bb";
	  public static final String AUTH_TOKEN = "c915af32991a11ce79e26adffe04b448";

	  private String toPhoneNumber;
	  
	  private String message;
	  
	  public SMSNotification(long toPhoneNumber, String message) {
		this.toPhoneNumber = "+" + toPhoneNumber;
		this.message = message;
	  }
	  @Override
		public void run() {
	    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

	    Message message = Message.creator(new PhoneNumber(this.toPhoneNumber),
	        new PhoneNumber("+12184963670"), 
	        this.message).create();

	    System.out.println(message.getSid());
	  }

}
