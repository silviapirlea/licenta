package com.example.licenta.appointments.mapper;

import com.example.licenta.appointments.dto.AppointmentDto;
import com.example.licenta.appointments.model.Appointment;
import com.example.licenta.appointments.model.AppointmentStatusEnum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "id", source = "appointment.uuid")
    @Mapping(target = "internal", ignore = true)
    AppointmentDto toDtoIgnoreInternal(Appointment appointment, AppointmentStatusEnum status);

    @Mapping(target = "id", source = "appointment.uuid")
    @Mapping(target = "internal", source = "appointment.internal")
    AppointmentDto toDto(Appointment appointment, AppointmentStatusEnum status);
}
