
package com.publicis.sapient.recipeapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.tree.RowMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.ModelMapExtensionsKt;
import org.springframework.web.client.RestTemplate;

import com.publicis.sapient.recipeapi.Application;
import com.publicis.sapient.recipeapi.binding.RecipeBinding;
import com.publicis.sapient.recipeapi.binding.RecipeListBinding;
import com.publicis.sapient.recipeapi.dto.RecipeDto;
import com.publicis.sapient.recipeapi.dto.RecipeDto.RecipeDtoBuilder;
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

@SpringBootTest()
@ExtendWith(MockitoExtension.class)
public class RecipeApiServiceImplTest {

	@MockitoBean
	public RecipeApiRepo recipeApiRepo;

	@MockitoBean
	public RecipeMapper recipeMapper;

	@MockitoBean
	ExampleMatcher exampleMatcher;

	@MockitoBean
	RestTemplate restTemplate;

	@MockitoBean
	ModelMapper modelMapper;

	@MockitoBean
	IngredientsRepo ingredientsRepo;

	@MockitoBean
	TagsRepo tagsRepo;

	@MockitoBean
	InstructionsRepo instructionsRepo;

	@MockitoBean
	MealTypeRepo mealRepo;

	@MockitoBean
	IngredientsEntity ingredientsEntity;

	@MockitoBean
	TagsEntity tagsEntity;

	@MockitoBean
	MealTypeEntity mealTypeEntity;

	@MockitoBean
	InstructionsEntity instructionsEntity;

	@MockitoBean
	RecipeEntity recipeEntity;

	@MockitoBean
	RecipeApiServiceImpl mockRecipeApiService;

	@InjectMocks
	public RecipeApiServiceImpl recipeApiService;

	@BeforeEach
	void setup() {
		ReflectionTestUtils.setField(recipeMapper, "modelMapper", modelMapper);
        ReflectionTestUtils.setField(recipeApiService, "recipesApiUrl", "https://dummyjson.com/recipes");

	}

	@Test
	public void testGetRecipeByIdSuccess() {
		Optional<RecipeEntity> recipeEntity = Optional.of(RecipeEntity.builder().id(1).name("Classic Margherita Pizza")
				.ingredients(new ArrayList<>(Arrays.asList(new IngredientsEntity(1, "Pizza dough"),
						new IngredientsEntity(2, "Tomato sauce"))))
				.instructions(
						new ArrayList<>(Arrays.asList(new InstructionsEntity(1, "Preheat the oven to 475°F (245°C)"),
								new InstructionsEntity(2, "Roll out the pizza dough and spread tomato sauce evenly"))))
				.prepTimeMinutes(20).cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine("Italian")
				.caloriesPerServing(300)
				.tags(new ArrayList<>(Arrays.asList(new TagsEntity(1, "Pizza"), new TagsEntity(2, "Italian"))))
				.userId(166).image("https://cdn.dummyjson.com/recipe-images/1.webp").rating((float) 4.6).reviewCount(76)
				.mealTypes(
						new ArrayList<>(Arrays.asList(new MealTypeEntity(1, "Dinner"), new MealTypeEntity(2, "Snack"))))
				.build());

		RecipeDto recipeDto = RecipeDto.builder().id(2).name("Classic Margherita Pizza")
				.ingredients(new ArrayList<>(Arrays.asList("Pizza dough", "ram")))
				.instructions(new ArrayList(Arrays.asList("Preheat the oven to 475°F (245°C).",
						"Roll out the pizza dough and spread tomato sauce evenly")))
				.prepTimeMinutes(20).cookTimeMinutes(15).servings(4).difficulty("Easy").cuisine(null)
				.caloriesPerServing(null).tags(null).userId(null).image(null).rating(null).reviewCount(null)
				.mealType(null).build();

		int recipeId = 1;
		System.out.println(recipeMapper);
		when(recipeApiRepo.findById(recipeId)).thenReturn(recipeEntity);
		when(recipeMapper.convertToRecipeDto(recipeEntity.get())).thenReturn(recipeDto);

		RecipeDto recipe = recipeApiService.getRecipeById(recipeId);

		assertNotNull(recipe);
		assertEquals(recipeDto, recipe);

	}

	@Test
	public void testGetRecipeByIdNotFound() {
		int recipeId = 31;
		when(recipeApiRepo.findById(recipeId)).thenReturn(Optional.empty());

		Exception exception = assertThrows(RecipeNotFoundException.class, () -> {
			recipeApiService.getRecipeById(recipeId);
		});

		assertEquals("Recipe not found with ID: 31", exception.getMessage());
	}

	@Test
	public void testFetchOrCreateEntitiesSucess() {

		List<String> ingredientsList = Arrays.asList("Tofu, cubed", "Broccoli florets", "Carrots, sliced",
				"Bell peppers, sliced");
		Function<List<String>, List<IngredientsEntity>> findFunction = ingredientsRepo::findExistingIngredients;
		Function<String, IngredientsEntity> createEntity = IngredientsEntity::new;
		Consumer<List<IngredientsEntity>> saveAll = ingredientsRepo::saveAll;

		List<IngredientsEntity> existingIngredients = new ArrayList<>();
		ingredientsList.forEach(name -> existingIngredients.add(new IngredientsEntity(name)));
		when(ingredientsRepo.findExistingIngredients(ingredientsList)).thenReturn(existingIngredients);

		List<IngredientsEntity> fetchOrCreateEntities = recipeApiService.fetchOrCreateEntities(ingredientsList,
				findFunction, createEntity, saveAll);

		assertEquals(4, fetchOrCreateEntities.size());
	}

	@Test
	public void testFetchOrCreateEntitiesSomeExisting() {
		List<String> names = Arrays.asList("Ingredient1", "Ingredient2", "Ingredient3");
		Function<List<String>, List<IngredientsEntity>> findExisting = ingredientsRepo::findExistingIngredients;
		Function<String, IngredientsEntity> createEntity = IngredientsEntity::new;
		Consumer<List<IngredientsEntity>> saveAll = ingredientsRepo::saveAll;

		IngredientsEntity existingIngredient = new IngredientsEntity("Ingredient1");
		when(ingredientsRepo.findExistingIngredients(names)).thenReturn(Collections.singletonList(existingIngredient));
		when(ingredientsRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		List<IngredientsEntity> result = recipeApiService.fetchOrCreateEntities(names, findExisting, createEntity,
				saveAll);

		assertEquals(3, result.size());
		verify(ingredientsRepo, times(1)).saveAll(anyList());
	}

	@Test
	public void testFetchOrCreateEntitiesAllNew() {
		List<String> names = Arrays.asList("Ingredient1", "Ingredient2", "Ingredient3");
		Function<List<String>, List<IngredientsEntity>> findExisting = ingredientsRepo::findExistingIngredients;
		Function<String, IngredientsEntity> createEntity = IngredientsEntity::new;
		Consumer<List<IngredientsEntity>> saveAll = ingredientsRepo::saveAll;

		when(ingredientsRepo.findExistingIngredients(names)).thenReturn(Collections.emptyList());
		when(ingredientsRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

		List<IngredientsEntity> result = recipeApiService.fetchOrCreateEntities(names, findExisting, createEntity,
				saveAll);

		assertEquals(3, result.size());
	}

	@Test
	public void testFetchOrCreateEntitiesItemsEmpty() {
		List<String> names = Collections.emptyList();
		Function<List<String>, List<IngredientsEntity>> findExisting = ingredientsRepo::findExistingIngredients;
		Function<String, IngredientsEntity> createEntity = IngredientsEntity::new;
		Consumer<List<IngredientsEntity>> saveAll = ingredientsRepo::saveAll;

		List<IngredientsEntity> result = recipeApiService.fetchOrCreateEntities(names, findExisting, createEntity,
				saveAll);

		assertEquals(Collections.emptyList(), result);
	}

	@Test
	public void testFetchOrCreateEntitiesItemsnull() {
		List<String> names = null;
		Function<List<String>, List<IngredientsEntity>> findExisting = ingredientsRepo::findExistingIngredients;
		Function<String, IngredientsEntity> createEntity = IngredientsEntity::new;
		Consumer<List<IngredientsEntity>> saveAll = ingredientsRepo::saveAll;

		List<IngredientsEntity> result = recipeApiService.fetchOrCreateEntities(names, findExisting, createEntity,
				saveAll);

		assertEquals(Collections.emptyList(), result);
	}

	@Test
	public void testGetEntityIdentifierIngredientsEntity() {
		String ingredientsname = "ingredientsname";
		when(ingredientsEntity.getIngredientsname()).thenReturn(ingredientsname);
		String result = recipeApiService.getEntityIdentifier(ingredientsEntity);
		assertNotNull(result);
	}

	@Test
	public void testGetEntityIdentifierTagsEntity() {
		String tagsname = "tagsname";
		when(tagsEntity.getTag()).thenReturn(tagsname);
		String result = recipeApiService.getEntityIdentifier(tagsEntity);
		assertNotNull(result);
	}

	@Test
	public void testGetEntityIdentifierMealTypeEntity() {
		when(mealTypeEntity.getMealType()).thenReturn("meal");
		String result = recipeApiService.getEntityIdentifier(mealTypeEntity);
		assertNotNull(result);
	}

	@Test
	public void testGetEntityIdentifierInstructionsEntity() {
		when(instructionsEntity.getInstruction()).thenReturn("instruction");
		String result = recipeApiService.getEntityIdentifier(instructionsEntity);
		assertNotNull(result);
	}

	@Test
	void testGetEntityIdentifierUnsupportedEntity() {
		Object unsupportedEntity = new Object();
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			recipeApiService.getEntityIdentifier(unsupportedEntity);
		});
		assertTrue(exception.getMessage().contains("Unsupported entity type"));
	}

	@Test
	void testGetRecipesByNameAndCuisineSuccess() {
		RecipeDto recipeDto = RecipeDto.builder().name("Pizza").cuisine("Italian").build();

		RecipeEntity recipeEntity = RecipeEntity.builder().name("Pizza").cuisine("Italian").build();

		List<RecipeEntity> recipeEntities = Arrays.asList(recipeEntity);
		List<RecipeDto> expectedDtos = Arrays.asList(recipeDto);

		when(recipeMapper.convertToEntity(recipeDto)).thenReturn(recipeEntity);
		when(recipeApiRepo.findAll(any(Example.class))).thenReturn(recipeEntities);
		when(recipeMapper.convertToDTOList(recipeEntities)).thenReturn(expectedDtos);

		List<RecipeDto> result = recipeApiService.getRecipesByNameAndCuisine(recipeDto);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(expectedDtos, result);

	}

	@Test
	void testGetRecipesByNameAndCuisineEmpty() {
		RecipeDto recipeDto = RecipeDto.builder().name("Pizza").cuisine("Italian").build();

		RecipeEntity recipeEntity = RecipeEntity.builder().name("Pizza").cuisine("Italian").build();

		List<RecipeEntity> recipeEntities = Arrays.asList(recipeEntity);

		when(recipeMapper.convertToEntity(recipeDto)).thenReturn(recipeEntity);
		when(recipeApiRepo.findAll(any(Example.class))).thenReturn(Collections.emptyList());

		List<RecipeDto> result = recipeApiService.getRecipesByNameAndCuisine(recipeDto);

		assertEquals(Collections.emptyList(), result);

	}

	@Test
	void testFetchExternalApiDataAndPersistApiReturnsNull() {
		String RECIPES_API_URL = "https://dummyjson.com/recipes";
		when(restTemplate.getForObject(RECIPES_API_URL, RecipeListBinding.class)).thenReturn(null);

		List<RecipeDto> result = recipeApiService.fetchExternalApiDataAndPersist();

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	@Test
	void testFetchExternalApiDataAndPersistEmptyRecipesList() {
		RecipeListBinding mockResponse = new RecipeListBinding();
		mockResponse.setRecipes(Collections.emptyList());

		String RECIPES_API_URL = "https://dummyjson.com/recipes";
		when(restTemplate.getForObject(eq(RECIPES_API_URL), eq(RecipeListBinding.class))).thenReturn(mockResponse);

		List<RecipeDto> result = recipeApiService.fetchExternalApiDataAndPersist();

		assertNotNull(result);
		assertTrue(result.isEmpty());

	}

	@Test
	void testFetchExternalApiDataAndPersistRecipesNull() {
		RecipeListBinding mockResponse = new RecipeListBinding();
		mockResponse.setRecipes(Collections.emptyList());

		String RECIPES_API_URL = "https://dummyjson.com/recipes";
		when(restTemplate.getForObject(eq(RECIPES_API_URL), eq(RecipeListBinding.class))).thenReturn(mockResponse);

		List<RecipeDto> result = recipeApiService.fetchExternalApiDataAndPersist();

		assertNotNull(result);
		assertTrue(result.isEmpty());

	}

	@Test
	void testFetchExternalApiDataAndPersistSuccess() {
		RecipeBinding recipe1 = RecipeBinding.builder().id(1).name("Pizza")
				.ingredients(Arrays.asList("Dough", "Cheese")).instructions(Arrays.asList("Bake at 200°C"))
				.mealType(Arrays.asList("Dinner")).tags(Arrays.asList("Italian")).build();

		RecipeListBinding mockResponse = new RecipeListBinding();
		mockResponse.setRecipes(Arrays.asList(recipe1));

		String RECIPES_API_URL = "https://dummyjson.com/recipes";
		when(restTemplate.getForObject(eq(RECIPES_API_URL), eq(RecipeListBinding.class))).thenReturn(mockResponse);

		RecipeEntity recipeEntity = new RecipeEntity();
		recipeEntity.setId(1);
		recipeEntity.setName("Pizza");
		recipeEntity.setIngredients(new ArrayList<>());
		recipeEntity.setInstructions(new ArrayList<>());
		recipeEntity.setMealTypes(new ArrayList<>());
		recipeEntity.setTags(new ArrayList<>());

		when(modelMapper.map(any(RecipeBinding.class), eq(RecipeEntity.class))).thenAnswer(invocation -> {
			RecipeBinding inputRecipe = invocation.getArgument(0);
			RecipeEntity entity = new RecipeEntity();
			entity.setId(inputRecipe.getId());
			entity.setName(inputRecipe.getName());
			entity.setIngredients(new ArrayList<>());
			entity.setInstructions(new ArrayList<>());
			entity.setMealTypes(new ArrayList<>());
			entity.setTags(new ArrayList<>());
			return entity;
		});

		when(ingredientsRepo.findExistingIngredients(anyList())).thenReturn(Collections.emptyList());
		when(tagsRepo.findExistingTag(anyList())).thenReturn(Collections.emptyList());
		when(mealRepo.findExistingMealType(anyList())).thenReturn(Collections.emptyList());
		when(instructionsRepo.findExistingInstruction(anyList())).thenReturn(Collections.emptyList());

		when(ingredientsRepo.saveAll(anyList())).thenReturn(Collections.emptyList());
		when(tagsRepo.saveAll(anyList())).thenReturn(Collections.emptyList());
		when(mealRepo.saveAll(anyList())).thenReturn(Collections.emptyList());
		when(instructionsRepo.saveAll(anyList())).thenReturn(Collections.emptyList());

		when(recipeApiRepo.saveAll(anyList())).thenReturn(Arrays.asList(recipeEntity));

		RecipeDto expectedDto = RecipeDto.builder().id(1).name("Pizza").build();

		when(recipeMapper.convertToDTOList(anyList())).thenReturn(Arrays.asList(expectedDto));

		List<RecipeDto> result = recipeApiService.fetchExternalApiDataAndPersist();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Pizza", result.get(0).getName());

	}

	@Test
	    public void testIsRecipeDataEmptyTrue() {
	    	when(recipeApiRepo.count()).thenReturn((long) 0);
	    	
	    	Boolean result = recipeApiService.isRecipeDataEmpty();
	    	assertTrue(result);
	    }

	@Test
	    public void testIsRecipeDataEmptyFalse() {
	    	when(recipeApiRepo.count()).thenReturn((long) 1);
	    	
	    	Boolean result = recipeApiService.isRecipeDataEmpty();
	    	assertFalse(result);
	    }
}
