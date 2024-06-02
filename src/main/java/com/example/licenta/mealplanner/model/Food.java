package com.example.licenta.mealplanner.model;

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
@Table(name = "foods", uniqueConstraints = {@UniqueConstraint(columnNames = "name")})
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Size(max = 50)
    private String name;
    @ColumnDefault("100")
    private Integer grams;
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
}
