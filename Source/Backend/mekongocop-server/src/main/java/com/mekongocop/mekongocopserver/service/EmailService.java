package com.mekongocop.mekongocopserver.service;

import com.mekongocop.mekongocopserver.repository.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailParseException;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final EmailSender emailSender;

    public EmailService(MailjetEmailService mailjetEmailService) {
        this.emailSender = mailjetEmailService;
    }

    public void sendOTPEmail(String to, String otp) {
        // Trim the email to remove leading/trailing spaces
        to = to.trim();

        // Validate email address
        if (to == null || !to.matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("Invalid email address");
        }

        // Log the email address for debugging
        System.out.println("Sending OTP to email: " + to);

        try {
            emailSender.sendOTPEmail(to, otp);
            logger.info("OTP Sent Successfully");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new MailParseException("Could not parse mail", e);
        }
    }
}