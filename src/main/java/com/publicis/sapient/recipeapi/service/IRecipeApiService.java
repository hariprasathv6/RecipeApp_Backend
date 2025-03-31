package com.publicis.sapient.recipeapi.service;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.publicis.sapient.recipeapi.dto.RecipeDto;

public interface IRecipeApiService {

	public List<RecipeDto> fetchExternalApiDataAndPersist();

	public RecipeDto getRecipeById(Integer recipeId);

	public List<RecipeDto> getRecipesByNameAndCuisine(RecipeDto recipeDto);

	public String getEntityIdentifier(Object entity);

	public <T, R> List<R> fetchOrCreateEntities(List<T> items, Function<List<T>, List<R>> findFunction,
			Function<T, R> createFunction, Consumer<List<R>> saveFunction);

	public boolean isRecipeDataEmpty();
}
