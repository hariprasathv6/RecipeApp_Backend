package com.publicis.sapient.recipeapi.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.anything;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestTemplate;

import com.publicis.sapient.recipeapi.dto.RecipeDto;
import com.publicis.sapient.recipeapi.entity.IngredientsEntity;
import com.publicis.sapient.recipeapi.entity.InstructionsEntity;
import com.publicis.sapient.recipeapi.entity.MealTypeEntity;
import com.publicis.sapient.recipeapi.entity.RecipeEntity;
import com.publicis.sapient.recipeapi.entity.TagsEntity;

import aj.org.objectweb.asm.commons.Method;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class RecipeMapperTest {

	@Mock
	private ModelMapper modelMapper;

	@InjectMocks
	private RecipeMapper recipeMapper;

	@Mock
	private RecipeMapper recipeMapperMock;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testConvertToStringList() {
		List<MealTypeEntity> sourceList = Arrays.asList(
				MealTypeEntity.builder().mealTypeId(1).mealType("chicken").build(),
				MealTypeEntity.builder().mealTypeId(1).mealType("soup").build(), null);

		List<String> result = recipeMapper.convertToStringList(sourceList, src -> src.getMealType());

		List<String> expected = Arrays.asList("chicken", "soup");
		assertEquals(expected, result);
	}

	@Test
	void testConvertToStringListWithNullExtractor() {
		List<MealTypeEntity> sourceList = Arrays.asList(
				MealTypeEntity.builder().mealTypeId(1).mealType("chicken").build(),
				MealTypeEntity.builder().mealTypeId(1).mealType("soup").build(), null);

		List<String> result = recipeMapper.convertToStringList(sourceList, s -> null);

		assertTrue(result.isEmpty());
	}

	@Test
	void testConvertToStringListWithEmptyList() {
		List<Integer> input = Arrays.asList();
		Function<Integer, String> extractor = Object::toString;

		List<String> result = recipeMapper.convertToStringList(input, extractor);

		assertTrue(result.isEmpty());
	}

	@Test
	void testConvertToStringListWithNullList() {
		Function<Integer, String> extractor = Object::toString;

		assertThrows(NullPointerException.class, () -> recipeMapper.convertToStringList(null, extractor));
	}

	@Test
	void testConvertToRecipeDto() {
		RecipeEntity recipeEntity = new RecipeEntity();
		recipeEntity.setId(1);
		recipeEntity.setIngredients(Arrays.asList(new IngredientsEntity("Salt"), new IngredientsEntity("Pepper")));
		recipeEntity.setInstructions(
				Arrays.asList(new InstructionsEntity("Mix well"), new InstructionsEntity("Cook for 10 minutes")));
		recipeEntity.setTags(Arrays.asList(new TagsEntity("Spicy"), new TagsEntity("Vegetarian")));
		recipeEntity.setMealTypes(Arrays.asList(new MealTypeEntity("Dinner"), new MealTypeEntity("Lunch")));

		RecipeDto recipeDtoMock = new RecipeDto();
		when(modelMapper.map(recipeEntity, RecipeDto.class)).thenReturn(recipeDtoMock);

		RecipeDto result = recipeMapper.convertToRecipeDto(recipeEntity);

		assertNotNull(result);
		assertEquals(Arrays.asList("Salt", "Pepper"), result.getIngredients());
		assertEquals(Arrays.asList("Mix well", "Cook for 10 minutes"), result.getInstructions());
		assertEquals(Arrays.asList("Spicy", "Vegetarian"), result.getTags());
		assertEquals(Arrays.asList("Dinner", "Lunch"), result.getMealType());
	}

	@Test
	void testConvertToRecipeDtoWithNullFields() {
		RecipeEntity recipeEntity = new RecipeEntity();
		recipeEntity.setId(2);
		recipeEntity.setIngredients(null);
		recipeEntity.setInstructions(null);
		recipeEntity.setTags(null);
		recipeEntity.setMealTypes(null);

		RecipeDto recipeDtoMock = new RecipeDto();
		when(modelMapper.map(recipeEntity, RecipeDto.class)).thenReturn(recipeDtoMock);

		RecipeDto result = recipeMapper.convertToRecipeDto(recipeEntity);

		assertNotNull(result);
		assertEquals(Collections.emptyList(), result.getIngredients());
		assertEquals(Collections.emptyList(), result.getInstructions());
		assertEquals(Collections.emptyList(), result.getTags());
		assertEquals(Collections.emptyList(), result.getMealType());
	}

	@Test
	void testConvertToDTOListEmpty() {
		List<RecipeDto> result = recipeMapper.convertToDTOList(null);

		assertEquals(Collections.emptyList(), result);
	}

	@Test
	void testConvertToDTOListSuccess() {
		RecipeEntity recipe1 = RecipeEntity.builder().id(1).name("Classic Margherita Pizza")
				.ingredients(Arrays.asList(new IngredientsEntity("Pizza dough"), new IngredientsEntity("Tomato sauce"),
						new IngredientsEntity("Fresh mozzarella cheese"), new IngredientsEntity("Fresh basil leaves"),
						new IngredientsEntity("Olive oil"), new IngredientsEntity("Salt and pepper to taste")))
				.instructions(Arrays.asList(new InstructionsEntity("Preheat the oven to 475°F (245°C)."),
						new InstructionsEntity("Roll out the pizza dough and spread tomato sauce evenly."),
						new InstructionsEntity("Top with slices of fresh mozzarella and fresh basil leaves."),
						new InstructionsEntity(
								"Bake in the preheated oven for 12-15 minutes or until the crust is golden brown."),
						new InstructionsEntity("Slice and serve hot.")))
				.mealTypes(Arrays.asList(new MealTypeEntity("Dinner")))
				.tags(Arrays.asList(new TagsEntity("Pizza"), new TagsEntity("Italian"))).prepTimeMinutes(20)
				.cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine("Italian").caloriesPerServing(300)
				.userId(166).image("https://cdn.dummyjson.com/recipe-images/1.webp").rating(4.6f).reviewCount(98)
				.build();

		RecipeEntity recipe2 = RecipeEntity.builder().id(2).name("Vegetarian Stir-Fry")
				.ingredients(Arrays.asList(new IngredientsEntity("Tofu, cubed"),
						new IngredientsEntity("Broccoli florets"), new IngredientsEntity("Carrots, sliced"),
						new IngredientsEntity("Sesame oil"), new IngredientsEntity("Cooked rice for serving")))
				.instructions(Arrays.asList(new InstructionsEntity("In a wok, heat sesame oil over medium-high heat."),
						new InstructionsEntity("Add minced ginger and garlic, sauté until fragrant."),
						new InstructionsEntity("Add cubed tofu and stir-fry until golden brown."),
						new InstructionsEntity("Serve over cooked rice.")))
				.mealTypes(Arrays.asList(new MealTypeEntity("Lunch")))
				.tags(Arrays.asList(new TagsEntity("Vegetarian"), new TagsEntity("Stir-fry"), new TagsEntity("Asian")))
				.prepTimeMinutes(15).cookTimeMinutes(20).servings(3).difficulty("Medium").cuisine("Asian")
				.caloriesPerServing(250).userId(143).image("https://cdn.dummyjson.com/recipe-images/2.webp")
				.rating(4.7f).reviewCount(26).build();

		RecipeDto recipeDto1 = RecipeDto.builder().id(1).name("Classic Margherita Pizza")
				.ingredients(Arrays.asList("Pizza dough", "Tomato sauce", "Fresh mozzarella cheese",
						"Fresh basil leaves", "Olive oil", "Salt and pepper to taste"))
				.instructions(Arrays.asList("Preheat the oven to 475°F (245°C).",
						"Roll out the pizza dough and spread tomato sauce evenly.",
						"Top with slices of fresh mozzarella and fresh basil leaves.",
						"Bake in the preheated oven for 12-15 minutes or until the crust is golden brown.",
						"Slice and serve hot."))
				.tags(Arrays.asList("Pizza", "Italian")).mealType(Arrays.asList("Dinner")).prepTimeMinutes(20)
				.cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine("Italian").caloriesPerServing(300)
				.userId(166).image("https://cdn.dummyjson.com/recipe-images/1.webp").rating(4.6f).reviewCount(98)
				.build();

		RecipeDto recipeDto2 = RecipeDto.builder().id(2).name("Vegetarian Stir-Fry")
				.ingredients(Arrays.asList("Tofu, cubed", "Broccoli florets", "Carrots, sliced", "Sesame oil",
						"Cooked rice for serving"))
				.instructions(Arrays.asList("In a wok, heat sesame oil over medium-high heat.",
						"Add minced ginger and garlic, sauté until fragrant.",
						"Add cubed tofu and stir-fry until golden brown.", "Serve over cooked rice."))
				.tags(Arrays.asList("Vegetarian", "Stir-fry", "Asian")).mealType(Arrays.asList("Lunch"))
				.prepTimeMinutes(15).cookTimeMinutes(20).servings(3).difficulty("Medium").cuisine("Asian")
				.caloriesPerServing(250).userId(143).image("https://cdn.dummyjson.com/recipe-images/2.webp")
				.rating(4.7f).reviewCount(26).build();

		List<RecipeDto> excepted = Arrays.asList(recipeDto1, recipeDto1);
		List<RecipeEntity> recipeEntites = Arrays.asList(recipe1, recipe2);

		when(recipeMapperMock.convertToRecipeDto(recipeEntites.get(0))).thenReturn(recipeDto1);
		when(recipeMapperMock.convertToRecipeDto(recipeEntites.get(1))).thenReturn(recipeDto2);
		when(recipeMapperMock.convertToDTOList(recipeEntites)).thenCallRealMethod();

		List<RecipeDto> result = recipeMapperMock.convertToDTOList(recipeEntites);

		assertEquals(2, result.size());
	}

	@Test
	public void testConvertToEntity() {
		RecipeDto recipeDto1 = RecipeDto.builder().id(1).name("Classic Margherita Pizza")
				.ingredients(Arrays.asList("Pizza dough", "Tomato sauce", "Fresh mozzarella cheese",
						"Fresh basil leaves", "Olive oil", "Salt and pepper to taste"))
				.instructions(Arrays.asList("Preheat the oven to 475°F (245°C).",
						"Roll out the pizza dough and spread tomato sauce evenly.",
						"Top with slices of fresh mozzarella and fresh basil leaves.",
						"Bake in the preheated oven for 12-15 minutes or until the crust is golden brown.",
						"Slice and serve hot."))
				.tags(Arrays.asList("Pizza", "Italian")).mealType(Arrays.asList("Dinner")).prepTimeMinutes(20)
				.cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine("Italian").caloriesPerServing(300)
				.userId(166).image("https://cdn.dummyjson.com/recipe-images/1.webp").rating(4.6f).reviewCount(98)
				.build();

		RecipeEntity recipe1 = RecipeEntity.builder().id(1).name("Classic Margherita Pizza")
				.ingredients(Arrays.asList(new IngredientsEntity("Pizza dough"), new IngredientsEntity("Tomato sauce"),
						new IngredientsEntity("Fresh mozzarella cheese"), new IngredientsEntity("Fresh basil leaves"),
						new IngredientsEntity("Olive oil"), new IngredientsEntity("Salt and pepper to taste")))
				.instructions(Arrays.asList(new InstructionsEntity("Preheat the oven to 475°F (245°C)."),
						new InstructionsEntity("Roll out the pizza dough and spread tomato sauce evenly."),
						new InstructionsEntity("Top with slices of fresh mozzarella and fresh basil leaves."),
						new InstructionsEntity(
								"Bake in the preheated oven for 12-15 minutes or until the crust is golden brown."),
						new InstructionsEntity("Slice and serve hot.")))
				.mealTypes(Arrays.asList(new MealTypeEntity("Dinner")))
				.tags(Arrays.asList(new TagsEntity("Pizza"), new TagsEntity("Italian"))).prepTimeMinutes(20)
				.cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine("Italian").caloriesPerServing(300)
				.userId(166).image("https://cdn.dummyjson.com/recipe-images/1.webp").rating(4.6f).reviewCount(98)
				.build();
		when(recipeMapperMock.convertToEntity(recipeDto1)).thenCallRealMethod();
		recipeMapperMock.modelMapper = modelMapper;
		when(recipeMapperMock.modelMapper.map(recipeDto1, RecipeEntity.class)).thenReturn(recipe1);

		RecipeEntity result = recipeMapperMock.convertToEntity(recipeDto1);

		assertEquals(recipe1, result);
	}

	@Test
	public void testConfigureModelMapper() {
		recipeMapper = new RecipeMapper();

		Exception exception = assertThrows(InvocationTargetException.class, () -> {
			java.lang.reflect.Method postConstructMethod = RecipeMapper.class.getDeclaredMethod("configureModelMapper");
			postConstructMethod.setAccessible(true);
			postConstructMethod.invoke(recipeMapper);
		});
		assertEquals("ModelMapper instance is null. Cannot configure mappings.", exception.getCause().getMessage());

	}

}
