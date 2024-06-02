package com.example.licenta.appointments.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "appointments", uniqueConstraints = {@UniqueConstraint(columnNames = {"hour", "date"})})
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String uuid;
    private String username;
    private LocalDate date;
//    @Enumerated(EnumType.STRING)
//    private HoursEnum hour;
    private String hour;
    @Size(max = 50)
    private String firstName;
    @Size(max = 50)
    private String lastName;
    @Size(max = 15)
    private String phone;
    @Size(max = 100)
    private String message;

    @ColumnDefault("false")
    private boolean internal;

    private String locationDetails;
}
