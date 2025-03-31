package com.publicis.sapient.recipeapi.dto;

import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDto {
    private Integer id;
    private String name;
    private List<String> ingredients;
    private List<String> instructions;
    private List<String> tags;
    private List<String> mealType;
    private Integer prepTimeMinutes;
    private Integer cookTimeMinutes;
    private Integer servings;
    private String difficulty;
    private String cuisine;
    private Integer caloriesPerServing;
    private Integer userId;
    private String image;
    private Float rating;
    private Integer reviewCount;

}
