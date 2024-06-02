package com.example.licenta.userboard.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user-profile", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    @NotBlank
    @Size(max = 50)
    private String firstName;
    @NotBlank
    @Size(max = 50)
    private String lastName;
    @NotBlank
    @Size(max = 3)
    private String age;
    @NotBlank
    private String sex;
    @NotBlank
    @Size(max = 10)
    private String height;
    @NotBlank
    @Size(max = 10)
    private String weight;
    @NotBlank
    @Size(max = 10)
    private String targetWeight;
    @Size(max = 100)
    private String goals;
    @Size(max = 100)
    private String activeness;
    @Size(max = 100)
    private String sportLevel;
    @Size(max = 100)
    private String badHabits;
    @Size(max = 100)
    private String sleepHours;
    @Size(max = 50)
    private String water;
    @Size(max = 100)
    private String allergies;
    @Size(max = 50)
    private String alimentationType;
    @Size(max = 150)
    private String preferredFoods;
    @ColumnDefault("false")
    private boolean hasMealPlan;
}
