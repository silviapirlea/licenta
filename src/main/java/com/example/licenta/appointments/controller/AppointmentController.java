package com.example.licenta.appointments.controller;

import com.example.licenta.appointments.dto.AppointmentDto;
import com.example.licenta.appointments.model.Appointment;
import com.example.licenta.appointments.model.AppointmentPage;
import com.example.licenta.appointments.service.AppointmentService;
import com.example.licenta.auth.payload.response.MessageResponse;
import com.example.licenta.exceptions.BadRequestException;
import com.example.licenta.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping()
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> makeAppointment(@Valid @RequestBody Appointment appointment) {
        try {
            return ResponseEntity.ok(appointmentService.saveAppointment(appointment));
        } catch (BadRequestException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(exception.getMessage()));
        }
    }

    @PostMapping("/internal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> makeInternalAppointment(@Valid @RequestBody Appointment appointment) {
        try {
            return ResponseEntity.ok(appointmentService.saveInternalAppointment(appointment));
        } catch (BadRequestException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(exception.getMessage()));
        }
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelAppointment(@PathVariable String uuid) {
        try {
            appointmentService.cancelAppointment(uuid);
            return ResponseEntity.ok(new MessageResponse("Appointment canceled!"));
        }catch (BadRequestException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(exception.getMessage()));
        }catch (ResourceNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(exception.getMessage()));
        }
    }

    @DeleteMapping("/internal/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> cancelInternalAppointment(@PathVariable String uuid) {
        try {
            appointmentService.cancelInternalAppointment(uuid);
            return ResponseEntity.ok(new MessageResponse("Appointment canceled!"));
        }catch (ResourceNotFoundException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(exception.getMessage()));
        }
    }

    @GetMapping("/unavailable")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getUnavailableHours(@RequestParam(value = "date", required = true) LocalDate date) {
        try {
            return ResponseEntity.ok(appointmentService.getUnavailableHours(date));
        } catch (BadRequestException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(exception.getMessage()));
        }
    }

    @GetMapping("/internal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsPerDay(@RequestParam(value = "date", required = true) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForADay(date));
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<AppointmentPage> getUserAppointments(
            @PathVariable String username,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "2", required = false) int size
            ) {
        return ResponseEntity.ok(appointmentService.getAppointmentsForUser(page, size, username));
    }
}
