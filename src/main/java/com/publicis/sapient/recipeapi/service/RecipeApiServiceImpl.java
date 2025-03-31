package com.publicis.sapient.recipeapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.publicis.sapient.recipeapi.binding.RecipeBinding;
import com.publicis.sapient.recipeapi.binding.RecipeListBinding;
import com.publicis.sapient.recipeapi.dto.RecipeDto;
import com.publicis.sapient.recipeapi.entity.IngredientsEntity;
import com.publicis.sapient.recipeapi.entity.InstructionsEntity;
import com.publicis.sapient.recipeapi.entity.MealTypeEntity;
import com.publicis.sapient.recipeapi.entity.RecipeEntity;
import com.publicis.sapient.recipeapi.entity.TagsEntity;
import com.publicis.sapient.recipeapi.exception.RecipeNotFoundException;
import com.publicis.sapient.recipeapi.mapper.RecipeMapper;
import com.publicis.sapient.recipeapi.repo.IngredientsRepo;
import com.publicis.sapient.recipeapi.repo.InstructionsRepo;
import com.publicis.sapient.recipeapi.repo.MealTypeRepo;
import com.publicis.sapient.recipeapi.repo.RecipeApiRepo;
import com.publicis.sapient.recipeapi.repo.TagsRepo;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecipeApiServiceImpl implements IRecipeApiService {

	@Autowired
	RecipeApiRepo recipeRepo;

	@Autowired
	IngredientsRepo ingredientsRepo;

	@Autowired
	InstructionsRepo instructionsRepo;

	@Autowired
	TagsRepo tagsRepo;

	@Autowired
	MealTypeRepo mealRepo;

	@Autowired
	RecipeMapper recipeMapper;

	@Autowired
	RestTemplate restTemplate;
	
	@Value("${recipes.api.url}")
	String recipesApiUrl;
	
	public String getEntityIdentifier(Object entity) {
		if (entity instanceof IngredientsEntity) {
			return ((IngredientsEntity) entity).getIngredientsname();
		} else if (entity instanceof TagsEntity) {
			return ((TagsEntity) entity).getTag();
		} else if (entity instanceof MealTypeEntity) {
			return ((MealTypeEntity) entity).getMealType();
		} else if (entity instanceof InstructionsEntity) {
			return ((InstructionsEntity) entity).getInstruction();
		} else {
			log.warn("RecipeApiServiceImpl :: getEntityIdentifier : Unsupported entity type: {}",
					entity.getClass().getName());
			throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass().getName());
		}
	}

	public <T, R> List<R> fetchOrCreateEntities(List<T> items, Function<List<T>, List<R>> findFunction,
			Function<T, R> createFunction, Consumer<List<R>> saveFunction) {

		if (Objects.isNull(items) || items.isEmpty()) {
			log.info("RecipeApiServiceImpl :: fetchOrCreateEntities : No items provided, returning empty list.");
			return Collections.emptyList();
		}

		log.info("RecipeApiServiceImpl :: fetchOrCreateEntities : Checking for existing entities in database.");

		List<R> existingEntities = findFunction.apply(items);
		Set<String> existingEntityIdentifiers = existingEntities.stream().map(this::getEntityIdentifier)
				.collect(Collectors.toSet());

		log.info("RecipeApiServiceImpl :: fetchOrCreateEntities : Found {} existing entities.",
				existingEntities.size());

		List<R> newEntities = items.stream().filter(item -> !existingEntityIdentifiers.contains(item.toString()))
				.map(createFunction).collect(Collectors.toList());

		if (!newEntities.isEmpty()) {
			log.info("RecipeApiServiceImpl :: fetchOrCreateEntities : Saving {} new entities to the database.",
					newEntities.size());
			saveFunction.accept(newEntities);
			existingEntities = new ArrayList<>(existingEntities);
			existingEntities.addAll(newEntities);
		}
		return existingEntities;
	}

	@Transactional
	public List<RecipeDto> fetchExternalApiDataAndPersist() {
		log.info("RecipeApiServiceImpl :: fetchExternalApiDataAndPersist :: Fetching recipes from external API: {}",
				recipesApiUrl);

		RecipeListBinding response = restTemplate.getForObject(recipesApiUrl, RecipeListBinding.class);

		if (response == null) {
			log.warn(
					"RecipeApiServiceImpl :: fetchExternalApiDataAndPersist :: API response is null. Terminating execution.");
			return Collections.emptyList();
		}

		List<RecipeBinding> recipes = response.getRecipes();

		if (recipes.isEmpty()) {
			log.warn("RecipeApiServiceImpl :: fetchExternalApiDataAndPersist :: No recipes found in external API.");
			return Collections.emptyList();
		}

		log.info("RecipeApiServiceImpl :: fetchExternalApiDataAndPersist :: Successfully fetched {} recipes.",
				recipes.size());

		List<RecipeEntity> recipeEntities = recipes.stream().map(recipe -> {
			RecipeEntity recipeEntity = recipeMapper.modelMapper.map(recipe, RecipeEntity.class);

			recipeEntity.setIngredients(fetchOrCreateEntities(recipe.getIngredients(),
					ingredientsRepo::findExistingIngredients, IngredientsEntity::new, ingredientsRepo::saveAll));

			recipeEntity.setTags(fetchOrCreateEntities(recipe.getTags(), tagsRepo::findExistingTag, TagsEntity::new,
					tagsRepo::saveAll));

			recipeEntity.setMealTypes(fetchOrCreateEntities(recipe.getMealType(), mealRepo::findExistingMealType,
					MealTypeEntity::new, mealRepo::saveAll));

			recipeEntity.setInstructions(fetchOrCreateEntities(recipe.getInstructions(),
					instructionsRepo::findExistingInstruction, InstructionsEntity::new, instructionsRepo::saveAll));

			return recipeEntity;
		}).collect(Collectors.toList());

		log.info("RecipeApiServiceImpl :: fetchExternalApiDataAndPersist :: Saving {} recipes to the database.",
				recipeEntities.size());

		recipeRepo.saveAll(recipeEntities);

		log.info(
				"RecipeApiServiceImpl :: fetchExternalApiDataAndPersist :: Successfully saved recipes to the database.");

		return recipeMapper.convertToDTOList(recipeEntities);
	}

	public List<RecipeDto> getRecipesByNameAndCuisine(RecipeDto recipeDto) {

		log.info("RecipeApiServiceImpl :: getRecipesByNameAndCuisine :: recipeDto:" + " " + recipeDto);

		ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase()
				.withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
				.withMatcher("cuisine", ExampleMatcher.GenericPropertyMatchers.contains());

		RecipeEntity recipeEntity = recipeMapper.convertToEntity(recipeDto);
		Example<RecipeEntity> of = Example.of(recipeEntity, matcher);
		List<RecipeEntity> recipes = recipeRepo.findAll(of);

		log.info("RecipeApiServiceImpl :: getRecipesByNameAndCuisine :: Found {} recipes:" + " " + recipes.size());
		return recipes.isEmpty() ? Collections.emptyList() : recipeMapper.convertToDTOList(recipes);
	}

	public RecipeDto getRecipeById(Integer recipeId) {

		return recipeRepo.findById(recipeId).map(recipe -> {
			log.info("RecipeApiServiceImpl :: getRecipeById : {}", recipe);
			return recipeMapper.convertToRecipeDto(recipe);
		}).orElseThrow(() -> new RecipeNotFoundException("Recipe not found with ID: " + recipeId));
	}

	public boolean isRecipeDataEmpty() {
		return recipeRepo.count() == 0;
	}
}
