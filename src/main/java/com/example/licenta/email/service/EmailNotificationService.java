package com.example.licenta.email.service;

import com.example.licenta.appointments.model.Appointment;
import com.example.licenta.auth.security.services.UserDetailsImpl;
import com.example.licenta.auth.service.UserService;
import com.example.licenta.email.model.Email;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {
    private static final String FROM = "digitaldietplanner@gmail.com";
    private final EmailService emailService;

    private final UserService userService;

    public void sendWelcomeEmail() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Email email = new Email();
        email.setTo(userDetails.getEmail());
        email.setFrom(FROM);
        email.setSubject("Welcome Email from CodingNConcepts");
        email.setTemplate("welcome-email.html");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "Ashish");
        properties.put("subscriptionDate", LocalDate.now().toString());
        properties.put("technologies", Arrays.asList("Python", "Go", "C#"));
        email.setProperties(properties);

        try {
            emailService.sendHtmlMessage(email);
        } catch (MessagingException e) {
            log.error("Could not sent the email");
            throw new RuntimeException(e);
        }
    }

    public void sendMealPlanUploadedEmail(String username) {
        var user = userService.findUserByUsername(username);
        Email email = new Email();
        email.setTo(user.getEmail());
        email.setFrom(FROM);
        email.setSubject("New meal plan uploaded!");
        email.setTemplate("meal-plan-uploaded.html");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", user.getUsername());
        email.setProperties(properties);

        try {
            emailService.sendHtmlMessage(email);
        } catch (MessagingException e) {
            log.error("Could not sent the email");
            throw new RuntimeException(e);
        }
    }

    public void sendNewAppointmentEmail(Appointment appointment) {
        Email email = new Email();
        email.setTo(FROM);
        email.setFrom(FROM);
        email.setSubject("New appointment!");
        email.setTemplate("new-appointment.html");
        email.setProperties(getAppointmentProperties(appointment));

        try {
            emailService.sendHtmlMessage(email);
        } catch (MessagingException e) {
            log.error("Could not sent the email");
            throw new RuntimeException(e);
        }
    }

    public void sendCanceledAppointmentEmail(Appointment appointment) {
        Email email = new Email();
        email.setTo(FROM);
        email.setFrom(FROM);
        email.setSubject("Appointment canceled!");
        email.setTemplate("canceled-appointment.html");
        email.setProperties(getAppointmentProperties(appointment));

        try {
            emailService.sendHtmlMessage(email);
        } catch (MessagingException e) {
            log.error("Could not sent the email");
            throw new RuntimeException(e);
        }
    }

    public void sentCanceledInternalAppointment(Appointment appointment) {
        var user = userService.findUserByUsername(appointment.getUsername());
        Email email = new Email();
        email.setTo(user.getEmail());
        email.setFrom(FROM);
        email.setSubject("Appointment canceled!");
        email.setTemplate("canceled-internal-appointment.html");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", appointment.getUsername());
        properties.put("date", appointment.getDate().toString());
        properties.put("hour", appointment.getHour());
        email.setProperties(properties);

        try {
            emailService.sendHtmlMessage(email);
        } catch (MessagingException e) {
            log.error("Could not sent the email");
            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> getAppointmentProperties(Appointment appointment) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", appointment.getUsername());
        properties.put("date", appointment.getDate().toString());
        properties.put("hour", appointment.getHour());
        properties.put("firstName", appointment.getFirstName());
        properties.put("lastName", appointment.getLastName());
        properties.put("phone", appointment.getPhone());
        properties.put("location", appointment.getLocationDetails());
        return properties;

    }
}
