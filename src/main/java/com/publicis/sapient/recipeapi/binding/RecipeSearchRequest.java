package com.publicis.sapient.recipeapi.binding;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RecipeSearchRequest {

	@NotBlank(message = "Recipe name is required.")
	@Pattern(regexp = "^[a-zA-Z \\(\\)-]+$", message = "Invalid 'name': Only letters and spaces allowed.")
	private String name;

	@Pattern(regexp = "^[a-zA-Z ]+$", message = "Invalid 'cuisine': Only letters and spaces allowed.")
	private String cuisine;
}
