package org.gobeshona.api.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Inject "from" email address from application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public boolean sendEmail(String email, String newPassword) {

        boolean status = false;

        // Create a SimpleMailMessage object
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail); // Use the injected email address
        message.setTo(email);
        message.setSubject("iQA Password Reset");
        message.setText("Ur new Password is : " + newPassword + ". Please change!");

        try {
            // Attempt to send the email
            mailSender.send(message);
            System.out.println("Email sent successfully to " + email);
            status=true;
        } catch (MailException e) {
            e.printStackTrace();
            // Handle the exception if email sending fails
            System.err.println("Error sending email: " + e.getMessage());
            // You can also log the error or take additional actions
            return false;
        }
        return false;
    }
}
