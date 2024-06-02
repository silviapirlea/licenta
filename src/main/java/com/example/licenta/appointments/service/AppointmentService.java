package com.example.licenta.appointments.service;

import com.example.licenta.appointments.dto.AppointmentDto;
import com.example.licenta.appointments.dto.UnavailableHoursDto;
import com.example.licenta.appointments.mapper.AppointmentMapper;
import com.example.licenta.appointments.model.Appointment;
import com.example.licenta.appointments.model.AppointmentPage;
import com.example.licenta.appointments.model.AppointmentStatusEnum;
import com.example.licenta.appointments.model.HoursEnum;
import com.example.licenta.appointments.repository.AppointmentRepository;
import com.example.licenta.auth.security.services.UserDetailsImpl;
import com.example.licenta.email.service.EmailNotificationService;
import com.example.licenta.exceptions.BadRequestException;
import com.example.licenta.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final EmailNotificationService emailNotificationService;
    private final AppointmentMapper appointmentMapper;

    public Appointment saveAppointment(Appointment appointment) {
        var day = appointment.getDate().getDayOfWeek();
        if(appointmentRepository.existsByDateAndHour(appointment.getDate(), appointment.getHour())) {
            throw new BadRequestException("Cannot make two appointments at the same hour in a day");
        }

        if(appointment.getDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Cannot make an appointment in a past day!");
        }

        if(appointmentRepository.existsByUsernameAndDateIsAfter(getCurrentUsername(), LocalDate.now())) {
            throw new BadRequestException("You already have an active appointment!");
        }

        if(day.equals(DayOfWeek.SATURDAY) || day.equals(DayOfWeek.SUNDAY)) {
            throw new BadRequestException("Cannot make appointments on weekend!");
        }

        appointment.setUuid(UUID.randomUUID().toString());
        appointment.setLocationDetails("Main Street, number 12, Cluj-Napoca");
        appointment.setUsername(getCurrentUsername());
        log.info("Adding a new appointment in db {}", appointment);
        var saved = appointmentRepository.save(appointment);
        emailNotificationService.sendNewAppointmentEmail(saved);
        return saved;
    }

    public Appointment saveInternalAppointment(Appointment appointment) {
        if(appointmentRepository.existsByDateAndHour(appointment.getDate(), appointment.getHour())) {
            throw new BadRequestException("Cannot make two appointments at the same hour in a day");
        }
        appointment.setUuid(UUID.randomUUID().toString());
        appointment.setInternal(true);
        log.info("Adding a new appointment in db {}", appointment);
        return appointmentRepository.save(appointment);
    }

    public void cancelAppointment(String uuid) {
        var appointment = appointmentRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment"));
        if(!Objects.equals(appointment.getUsername(), getCurrentUsername())) {
            throw new BadRequestException("Cannot delete this appointment!");
        }
        log.info("Canceling appointment with id {}", uuid);
        appointmentRepository.deleteById(appointment.getId());
        emailNotificationService.sendCanceledAppointmentEmail(appointment);
    }

    public void cancelInternalAppointment(String uuid) {
        var appointment = appointmentRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment"));
        log.info("Canceling appointment with id {}", uuid);
        appointmentRepository.deleteById(appointment.getId());
        if(!appointment.isInternal()) {
            emailNotificationService.sentCanceledInternalAppointment(appointment);
        }
    }

    public UnavailableHoursDto getUnavailableHours(LocalDate date) {
        var day = date.getDayOfWeek();
        if(day.equals(DayOfWeek.SATURDAY) || day.equals(DayOfWeek.SUNDAY)) {
            throw new BadRequestException("Cannot make appointments on weekend!");
        }
        var hours = appointmentRepository.findAllByDate(date).stream()
                .map(Appointment::getHour)
                .collect(Collectors.toList());
        return UnavailableHoursDto.builder().hours(hours).build();
    }

    public List<AppointmentDto> getAppointmentsForADay(LocalDate date) {
        return appointmentRepository.findAllByDate(date).stream()
                .map(appointment -> appointmentMapper.toDto(appointment, getAppointmentStatus(appointment)))
                .collect(Collectors.toList());
    }
    public AppointmentPage getAppointmentsForUser(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").ascending());
        var appointmentPage = appointmentRepository.findAllByUsername(pageable, username);
        List<AppointmentDto> dtoList = appointmentPage.getContent().stream()
                .map(appointment -> appointmentMapper.toDtoIgnoreInternal(appointment, getAppointmentStatus(appointment)))
                .collect(Collectors.toList());
        return AppointmentPage.builder()
                .page(appointmentPage.getNumber())
                .size(appointmentPage.getSize())
                .totalElements(appointmentPage.getTotalElements())
                .totalPages(appointmentPage.getTotalPages())
                .last(appointmentPage.isLast())
                .items(dtoList)
                .build();
    }

    private AppointmentStatusEnum getAppointmentStatus(Appointment appointment) {
        if(appointment.getDate().isBefore(LocalDate.now()))
            return AppointmentStatusEnum.EXPIRED;
        return AppointmentStatusEnum.ACTIVE;
    }

    private String getCurrentUsername() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUsername();
    }
}
