package com.publicis.sapient.recipeapi.rest;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyHbmImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JacksonInject.Value;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.publicis.sapient.recipeapi.binding.RecipeSearchRequest;
import com.publicis.sapient.recipeapi.dto.RecipeDto;
import com.publicis.sapient.recipeapi.entity.RecipeEntity;
import com.publicis.sapient.recipeapi.entity.RecipeEntity.RecipeEntityBuilder;
import com.publicis.sapient.recipeapi.exception.InvalidInputException;
import com.publicis.sapient.recipeapi.exception.RecipeNotFoundException;
import com.publicis.sapient.recipeapi.response.RecipeApiResponse;
import com.publicis.sapient.recipeapi.service.IRecipeApiService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@CrossOrigin()
@Slf4j
@RestController
public class RecipeApiRestController {

	@Autowired
	private IRecipeApiService recipeapiService;

	@PostMapping("/recipes/external-api")
	public ResponseEntity<RecipeApiResponse> fetchExternalApi() {
		log.info("RecipeApiController :: fetchExternalApi :: Initiating API data fetch.");

		List<RecipeDto> recipes = recipeapiService.fetchExternalApiDataAndPersist();
		RecipeApiResponse response = new RecipeApiResponse();
		response.setTime(LocalDateTime.now());
		if (recipes.isEmpty()) {
			log.warn("RecipeApiController :: fetchExternalApi :: No data fetched from external API.");
			response.setStatus(HttpStatus.NO_CONTENT.value());
			response.setMessage("No data fetched from external API.");
			return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);

		}

		log.info("RecipeApiController :: fetchExternalApi :: Fetched and stored data in DB Successfully");

		response.setStatus(HttpStatus.CREATED.value());
		response.setMessage("Data are fetched from Api and Stored to DB SuccessFully");
		response.setData(recipes);

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/recipe")
	public ResponseEntity<RecipeApiResponse> getRecipe(@RequestParam Integer recipeId) {

		log.info("RecipeApiController :: getRecipe :: Fetching recipe with ID: {}", recipeId);

		RecipeApiResponse response = new RecipeApiResponse();

		if (recipeapiService.isRecipeDataEmpty()) {
			throw new ResponseStatusException(HttpStatus.OK,
					"No recipes are available at the moment.try again after some time");
		}

		if (recipeId < 1) {
			throw new InvalidInputException("Recipe ID must be a positive integer starts with 1.");
		}

		RecipeDto recipe = recipeapiService.getRecipeById(recipeId);

		log.info("RecipeApiController :: getRecipe :: recipe:{}", recipe);

		response.setStatus(HttpStatus.OK.value());
		response.setMessage("Fetched recipes successfully");
		response.setData(recipe);
		response.setTime(LocalDateTime.now());

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@GetMapping("/recipes")
	public ResponseEntity<RecipeApiResponse> getRecipes(@Valid RecipeSearchRequest request) {

		log.info("RecipeApiController :: getRecipes :: RecipeSearchRequest:{}", request);

		if (recipeapiService.isRecipeDataEmpty()) {
			throw new ResponseStatusException(HttpStatus.OK,
					"No recipes are available at the moment.try again after some time");
		}

		RecipeApiResponse response = new RecipeApiResponse();
		RecipeDto recipeDto = new RecipeDto();

		recipeDto.setName(request.getName());

		if (request.getCuisine() != null) {
			recipeDto.setCuisine(request.getCuisine());
		}

		List<RecipeDto> recipes = recipeapiService.getRecipesByNameAndCuisine(recipeDto);

		log.info("RecipeApiController :: getRecipes :: recipe:{}", recipes);

		response.setTime(LocalDateTime.now());
		response.setData(recipes);

		if (recipes.isEmpty()) {
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("No recipes found");
			return new ResponseEntity<>(response, HttpStatus.OK);

		}

		response.setStatus(HttpStatus.OK.value());
		response.setMessage("Fetched recipes successfully");

		return new ResponseEntity<>(response, HttpStatus.OK);

	}

}
