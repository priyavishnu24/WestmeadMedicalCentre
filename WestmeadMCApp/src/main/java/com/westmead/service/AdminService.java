package com.westmead.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.westmead.document.Admin;
import com.westmead.exception.LoginFailureException;
import com.westmead.exception.RegistrationFailureException;
import com.westmead.notification.EmailNotification;
import com.westmead.notification.SMSNotification;
import com.westmead.repository.AdminRepository;

@Service
@PropertySource("classpath:notification.properties")
public class AdminService {

	private Logger logger = LoggerFactory.getLogger(DoctorService.class);

	@Autowired
	private AdminRepository adminRepo;

	@Autowired
	private Environment env;

	public String saveAdmin(Admin admin) throws RegistrationFailureException {
		admin.setEmailId(admin.getEmailId().toLowerCase());
		Optional<Admin> optionalUser = this.adminRepo.findById(admin.getEmailId());
		if (!optionalUser.isPresent()) {
			this.adminRepo.save(admin);

			// E-mail Notification
			String subject = env.getProperty("admin.register.email.subject");
			String content = env.getProperty("admin.register.email.body").replace("<ADMIN_NAME>", admin.getLastName());
			new Thread(new EmailNotification(admin.getEmailId(), subject, content)).start();
			//SMS Notification
			new Thread(new SMSNotification(admin.getPhoneNo(), content)).start();

			return "Success";
		} else {
			logger.info("E-mail id already registered");
			throw new RegistrationFailureException("E-mail id already registered");
		}
	}

	public Admin validateAdmin(String emailId, String password) throws LoginFailureException {
		logger.debug(emailId);
		logger.debug(password);
		Optional<Admin> optionalUser = this.adminRepo.findById(emailId.toLowerCase());
		if (optionalUser.isPresent()) {
			Admin admin = optionalUser.get();
			if (admin.getApproval().getStatus() == null) {
				logger.info("Please wait for admin to approve");
				throw new LoginFailureException("Please wait for admin to approve");
			} else if (admin.getApproval().getStatus().equals("rejected")) {
				logger.info("Contact Admin!! your approval has been rejected");
				throw new LoginFailureException("Contact Admin!! your approval has been rejected");
			}

			else if (admin.getApproval().getStatus().equals("approved") && admin.getPassword().equals(password)) {
				// admin.setPassword("");
				return admin;
			} else {
				logger.info("Password mismatch");
				throw new LoginFailureException("Password mismatch");
			}
		} else {
			logger.info("Email id is not registered");
			throw new LoginFailureException("Email id is not registered");
		}

	}

	public List<Admin> getNonApprovedAdmins() {
		return this.adminRepo.findNonApprovedAdmins();
	}

	public boolean approveAdmin(Admin admin) {
		Optional<Admin> optionalUser = this.adminRepo.findById(admin.getEmailId().toLowerCase());
		if (optionalUser.isPresent()) {
			Admin adminObj = optionalUser.get();
			adminObj.setApproval(admin.getApproval());
			this.adminRepo.save(adminObj);

			// E-mail Notification
			String subject = env.getProperty("admin.approved.email.subject");
			String content = env.getProperty("admin.approved.email.body").replace("<ADMIN_NAME>", admin.getLastName());
			new Thread(new EmailNotification(admin.getEmailId(), subject, content)).start();
			//SMS Notification
			new Thread(new SMSNotification(admin.getPhoneNo(), content)).start();
			
			return true;
		} else {
			return false;
		}
	}

	public void updateAdmin(Admin admin) {
		Admin adminDB = this.adminRepo.findById(admin.getEmailId().toLowerCase()).get();
		adminDB.setFirstName(admin.getFirstName());
		adminDB.setLastName(admin.getLastName());
		adminDB.setPassword(admin.getPassword());
		adminDB.setPhoneNo(admin.getPhoneNo());

		this.adminRepo.save(adminDB);

		// E-mail Notification
		String subject = env.getProperty("admin.updated.email.subject");
		String content = env.getProperty("admin.updated.email.body").replace("<ADMIN_NAME>", admin.getLastName());
		new Thread(new EmailNotification(admin.getEmailId(), subject, content)).start();
		//SMS Notification
		new Thread(new SMSNotification(admin.getPhoneNo(), content)).start();
	}
}
