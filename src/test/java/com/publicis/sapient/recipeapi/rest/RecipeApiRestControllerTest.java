package com.publicis.sapient.recipeapi.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.assertj.MockMvcTester.MockMvcRequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.publicis.sapient.recipeapi.binding.RecipeBinding;
import com.publicis.sapient.recipeapi.dto.RecipeDto;
import com.publicis.sapient.recipeapi.dto.RecipeDto.RecipeDtoBuilder;
import com.publicis.sapient.recipeapi.service.IRecipeApiService;

import lombok.Builder;

@WebMvcTest(value = RecipeApiRestController.class)
public class RecipeApiRestControllerTest {

	@MockitoBean
	private IRecipeApiService recipeApiService;

	@Autowired
	MockMvc mockMvc;

	@Test
	public void testFetchExternalApiRecipesEmpty() throws Exception {
		
		when(recipeApiService.fetchExternalApiDataAndPersist()).thenReturn(Collections.emptyList());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/recipes/external-api");
		
	     MvcResult result = mockMvc.perform(request).andReturn();
		
		MockHttpServletResponse response = result.getResponse();
		
		int actualResult = response.getStatus();
		
		int expectedResult = 204;
		
		assertEquals(expectedResult,actualResult);
	}

	@Test
	public void testFetchExternalApiRecipesSuccess() throws Exception {
		List<RecipeDto> recipesList = new ArrayList<>();
		RecipeDto recipe1 = RecipeDto.builder().id(2).name("Classic Margherita Pizza")
				.ingredients(new ArrayList<>(Arrays.asList("Pizza dough", "ram")))
				.instructions(new ArrayList(Arrays.asList("Preheat the oven to 475°F (245°C).",
						"Roll out the pizza dough and spread tomato sauce evenly")))
				.prepTimeMinutes(20).cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine(null)
				.caloriesPerServing(null).tags(null).userId(null).image(null).rating(null).reviewCount(null)
				.mealType(null).build();

		RecipeDto recipe2 = RecipeDto.builder().id(2).name("Classic Margherita Pizza")
				.ingredients(new ArrayList<>(Arrays.asList("Pizza dough", "ram")))
				.instructions(new ArrayList(Arrays.asList("Preheat the oven to 475°F (245°C).",
						"Roll out the pizza dough and spread tomato sauce evenly")))
				.prepTimeMinutes(20).cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine(null)
				.caloriesPerServing(null).tags(null).userId(null).image(null).rating(null).reviewCount(null)
				.mealType(null).build();

		recipesList.addAll(Arrays.asList(recipe1, recipe2));

		when(recipeApiService.fetchExternalApiDataAndPersist()).thenReturn(recipesList);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/recipes/external-api");

		MvcResult result = mockMvc.perform(request).andReturn();

		MockHttpServletResponse response = result.getResponse();

		int actualResult = response.getStatus();

		int expectedResult = 201;

		assertEquals(expectedResult, actualResult);
	}

	@Test
	public void testGetRecipeSuccess() throws Exception {

		RecipeDto recipe = RecipeDto.builder().id(2).name("Classic Margherita Pizza")
				.ingredients(new ArrayList<>(Arrays.asList("Pizza dough", "ram")))
				.instructions(new ArrayList(Arrays.asList("Preheat the oven to 475°F (245°C).",
						"Roll out the pizza dough and spread tomato sauce evenly")))
				.prepTimeMinutes(20).cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine(null)
				.caloriesPerServing(null).tags(null).userId(null).image(null).rating(null).reviewCount(null)
				.mealType(null).build();

		when(recipeApiService.getRecipeById(anyInt())).thenReturn(recipe);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/recipe?recipeId=1");

		MvcResult result = mockMvc.perform(request).andReturn();

		MockHttpServletResponse response = result.getResponse();

		int actualResult = response.getStatus();

		int expectedResult = 200;

		assertEquals(expectedResult, actualResult);

	}

	@Test
	public void testGetRecipeNotAvailableTest() throws Exception {

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/recipe?recipeId=-1");

		MvcResult result = mockMvc.perform(request).andReturn();

		MockHttpServletResponse response = result.getResponse();

		int actualResult = response.getStatus();

		int expectedResult = 400;

		assertEquals(expectedResult, actualResult);

	}

	@Test
	public void testGetRecipeEmpty() throws Exception {
		
	 when(recipeApiService.isRecipeDataEmpty()).thenReturn(true);
	
	MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/recipe?recipeId=1");
	
	MvcResult result = mockMvc.perform(request).andReturn();
	
	MockHttpServletResponse response = result.getResponse();
	
	int actualResult = response.getStatus();
	
	int expectedResult = 200;
	
	assertEquals(expectedResult,actualResult);
	
	}

	@Test
	public void testGetRecipesByCuisineAndNameEmpty() throws Exception {	
		
	 when(recipeApiService.isRecipeDataEmpty()).thenReturn(true);
	
	MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/recipes?cuisine=Italian&name=Classic Margherita");
	
	MvcResult result = mockMvc.perform(request).andReturn();
	
	MockHttpServletResponse response = result.getResponse();
	
	int actualResult = response.getStatus();
	
	int expectedResult = 200;
	
	assertEquals(expectedResult,actualResult);
	
	}

	@Test
	public void testGetRecipesByCuisineAndNameSuccess() throws Exception {
		List<RecipeDto> recipesList = new ArrayList<>();
		RecipeDto recipe1 = RecipeDto.builder().id(2).name("Classic Margherita Pizza")
				.ingredients(new ArrayList<>(Arrays.asList("Pizza dough", "ram")))
				.instructions(new ArrayList(Arrays.asList("Preheat the oven to 475°F (245°C).",
						"Roll out the pizza dough and spread tomato sauce evenly")))
				.prepTimeMinutes(20).cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine(null)
				.caloriesPerServing(null).tags(null).userId(null).image(null).rating(null).reviewCount(null)
				.mealType(null).build();

		RecipeDto recipe2 = RecipeDto.builder().id(2).name("Classic Margherita Pizza")
				.ingredients(new ArrayList<>(Arrays.asList("Pizza dough", "ram")))
				.instructions(new ArrayList(Arrays.asList("Preheat the oven to 475°F (245°C).",
						"Roll out the pizza dough and spread tomato sauce evenly")))
				.prepTimeMinutes(20).cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine(null)
				.caloriesPerServing(null).tags(null).userId(null).image(null).rating(null).reviewCount(null)
				.mealType(null).build();

		recipesList.addAll(Arrays.asList(recipe1, recipe2));

		when(recipeApiService.getRecipesByNameAndCuisine(any())).thenReturn(recipesList);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.get("/recipes?cuisine=Italian&name=Classic Margherita");

		MvcResult result = mockMvc.perform(request).andReturn();

		MockHttpServletResponse response = result.getResponse();

		int actualResult = response.getStatus();

		int expectedResult = 200;

		assertEquals(expectedResult, actualResult);

	}

	@Test
	public void testGetRecipesSuccess() throws Exception {	
		
	 when(recipeApiService.isRecipeDataEmpty()).thenReturn(true);
	
	MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/recipes?cuisine=ita&name=Class");
	
	MvcResult result = mockMvc.perform(request).andReturn();
	
	MockHttpServletResponse response = result.getResponse();
	
	int actualResult = response.getStatus();
	
	int expectedResult = 200;
	
	assertEquals(expectedResult,actualResult);
	
	}

	@Test
	public void testGetRecipesFail() throws Exception {

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/recipes?cuisine=3&name=2");

		MvcResult result = mockMvc.perform(request).andReturn();

		MockHttpServletResponse response = result.getResponse();

		int actualResult = response.getStatus();

		int expectedResult = 400;

		assertEquals(expectedResult, actualResult);

	}

	@Test
	public void testGetRecipesEmpty() throws Exception {	
		
	when(recipeApiService.getRecipesByNameAndCuisine(any())).thenReturn(Collections.emptyList());
	
	MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/recipes?cuisine=ita&name=Class");
	
	MvcResult result = mockMvc.perform(request).andReturn();
	
	MockHttpServletResponse response = result.getResponse();
	
	int actualResult = response.getStatus();
	
	int expectedResult = 200;
	
	assertEquals(expectedResult,actualResult);
	
	}

	@Test
	public void testGetRecipesByNameSuccess() throws Exception {	
		
	when(recipeApiService.getRecipesByNameAndCuisine(any())).thenReturn(Collections.emptyList());
	
	MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/recipes?name=Class");
	
	MvcResult result = mockMvc.perform(request).andReturn();
	
	MockHttpServletResponse response = result.getResponse();
	
	int actualResult = response.getStatus();
	
	int expectedResult = 200;
	
	assertEquals(expectedResult,actualResult);
	
	}

}
