package com.example.licenta.userboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private String username;
    private String firstName;
    private String lastName;
    private String age;
    private String sex;
    private String height;
    private String weight;
    private String targetWeight;
    private String goals;
    private String activeness;
    private String sportLevel;
    private String badHabits;
    private String sleepHours;
    private String water;
    private String allergies;
    private String alimentationType;
    private String preferredFoods;
}
