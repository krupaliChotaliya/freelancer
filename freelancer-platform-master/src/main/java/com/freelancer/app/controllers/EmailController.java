package com.freelancer.app.controllers;

import com.freelancer.app.models.EmailDetails;
import com.freelancer.app.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    @Autowired
    private EmailService emailService;

    // Sending a simple email
    @PostMapping("/sendMail")
    public ResponseEntity<String> sendMail(@RequestBody EmailDetails details) {
        try {
            System.out.println("enter>>>>>>>>>>>>>>>>>>>>>>>");
            String status = emailService.sendSimpleMail(details);
            return ResponseEntity.ok(status); // Return status with HTTP 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    // Sending email with attachment
    @PostMapping("/sendMailWithAttachment")
    public ResponseEntity<String> sendMailWithAttachment(@RequestBody EmailDetails details) {
        try {
            String status = emailService.sendMailWithAttachment(details);
            return ResponseEntity.ok(status); // Return status with HTTP 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send email with attachment: " + e.getMessage());
        }
    }
}
