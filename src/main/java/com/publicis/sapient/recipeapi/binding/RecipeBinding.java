package com.publicis.sapient.recipeapi.binding;

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
public class RecipeBinding {
	@NotNull(message = "ID cannot be null")
	@Min(value = 0, message = "Id must be non-negative")
	private Integer id;

	@NotNull(message = "Name cannot be Null")
	private String name;

	@NotNull(message = "Ingredients List cannot be Null")
	private List<String> ingredients;

	@NotNull(message = "Instructions List cannot be Null")
	private List<String> instructions;

	@NotNull(message = "Tags List cannot be Null")
	private List<String> tags;

	@NotNull(message = "MealType List cannot be Null")
	private List<String> mealType;

	@NotNull(message = "prepTimeMinutes cannot be Null")
	private Integer prepTimeMinutes;

	@NotNull(message = "cookTimeMinutes cannot be Null")
	private Integer cookTimeMinutes;

	@NotNull(message = "servings cannot be Null")
	private Integer servings;

	@NotNull(message = "difficulty cannot be Null")
	private String difficulty;

	@NotNull(message = "cuisine List cannot be Null")
	private String cuisine;

	@Min(value = 0, message = "Calories must be non-negative value")
	private Integer caloriesPerServing;

	@NotNull(message = "userId cannot be Null")
	private Integer userId;

	@NotNull(message = "Image cannot be Null")
	private String image;

	@NotNull(message = "Rating cannot be Null")
	@Min(value = 0, message = "rating must be non-negative")
	private Float rating;

	@NotNull(message = "reviewCount cannot be Null")
	@Min(value = 0, message = "ReviewCount must be non-negative")
	private Integer reviewCount;

}
