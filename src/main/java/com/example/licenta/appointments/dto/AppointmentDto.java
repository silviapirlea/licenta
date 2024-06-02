package com.example.licenta.appointments.dto;

import com.example.licenta.appointments.model.AppointmentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {
    private String id;
    private LocalDate date;
    private String hour;
    private String firstName;
    private String lastName;
    private String phone;
    private String message;
    private String locationDetails;
    private AppointmentStatusEnum status;
    private boolean internal;
}
