package com.example.licenta.appointments.model;

import com.example.licenta.appointments.dto.AppointmentDto;
import com.example.licenta.mealplanner.dto.RecipeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentPage {
    List<AppointmentDto> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
