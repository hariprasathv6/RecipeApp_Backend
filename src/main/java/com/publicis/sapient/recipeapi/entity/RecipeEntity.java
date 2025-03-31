package com.publicis.sapient.recipeapi.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_recipes")
public class RecipeEntity {

	@Id
	private Integer id;

	@Column(nullable = false, unique = true)
	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "t_recipes_ingredients", joinColumns = @JoinColumn(name = "recipe_id"), inverseJoinColumns = @JoinColumn(name = "ingredients_id"))
	private List<IngredientsEntity> ingredients;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "t_recipes_mealtype", joinColumns = @JoinColumn(name = "recipe_id"), inverseJoinColumns = @JoinColumn(name = "mealtype_id"))

	private List<MealTypeEntity> mealTypes;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "t_recipes_tags", joinColumns = @JoinColumn(name = "recipe_id"), inverseJoinColumns = @JoinColumn(name = "tags_id"))

	private List<TagsEntity> tags;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "t_recipe_instruction", joinColumns = @JoinColumn(name = "recipe_id"), inverseJoinColumns = @JoinColumn(name = "instruction_id"))
	private List<InstructionsEntity> instructions;

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
