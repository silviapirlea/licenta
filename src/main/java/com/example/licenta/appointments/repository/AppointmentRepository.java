package com.example.licenta.appointments.repository;

import com.example.licenta.appointments.model.Appointment;
import com.example.licenta.appointments.model.HoursEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
//    boolean existsByDateAndHour(LocalDate date, HoursEnum hour);
    boolean existsByDateAndHour(LocalDate date, String hour);
    boolean existsByUsernameAndDateIsAfter(String username, LocalDate date);
    Optional<Appointment> findByUuid(String uuid);

    void deleteByUuid(String uuid);
    List<Appointment> findAllByDate(LocalDate date);

    Page<Appointment> findAllByUsername(Pageable pageable, String username);
}
