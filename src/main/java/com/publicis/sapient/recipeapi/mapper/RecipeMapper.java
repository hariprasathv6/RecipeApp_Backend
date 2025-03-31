package com.publicis.sapient.recipeapi.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.publicis.sapient.recipeapi.dto.RecipeDto;
import com.publicis.sapient.recipeapi.entity.RecipeEntity;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RecipeMapper {

	@Autowired
	public ModelMapper modelMapper;

	public RecipeEntity convertToEntity(RecipeDto recipeDto) {
		return modelMapper.map(recipeDto, RecipeEntity.class);
	}

	@PostConstruct
	void configureModelMapper() {
		if (modelMapper == null) {
			throw new IllegalStateException("ModelMapper instance is null. Cannot configure mappings.");
		}
		modelMapper.addMappings(new PropertyMap<RecipeEntity, RecipeDto>() {
			@Override
			protected void configure() {
				map(source.getIngredients(), destination.getIngredients());
				map(source.getInstructions(), destination.getInstructions());
				map(source.getTags(), destination.getTags());
			}
		});
	}

	public List<RecipeDto> convertToDTOList(List<RecipeEntity> recipeEntities) {
		if (recipeEntities == null) {
			log.warn("RecipeMapper :: convertToDTOList: Input list is null. Returning an empty list.");
			return Collections.emptyList();
		}
		return recipeEntities.stream().map(this::convertToRecipeDto).collect(Collectors.toList());
	}

	public <T> List<String> convertToStringList(List<T> sourceList, Function<T, String> extractor) {

		return sourceList.stream().filter(Objects::nonNull).map(extractor).filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	public RecipeDto convertToRecipeDto(RecipeEntity recipeEntity) {

		log.info("RecipeMapper :: convertToRecipeDto: Converting RecipeEntity with ID: {}", recipeEntity.getId());
		List<String> ingredientNames = convertToStringList(
				Optional.ofNullable(recipeEntity.getIngredients()).orElse(Collections.emptyList()),
				ingredient -> ingredient.getIngredientsname());

		List<String> instructions = convertToStringList(
				Optional.ofNullable(recipeEntity.getInstructions()).orElse(Collections.emptyList()),
				instruction -> instruction.getInstruction());

		List<String> tags = convertToStringList(
				Optional.ofNullable(recipeEntity.getTags()).orElse(Collections.emptyList()), tag -> tag.getTag());

		List<String> mealTypes = convertToStringList(
				Optional.ofNullable(recipeEntity.getMealTypes()).orElse(Collections.emptyList()),
				meal -> meal.getMealType());

		RecipeDto recipeDto = modelMapper.map(recipeEntity, RecipeDto.class);
		recipeDto.setIngredients(ingredientNames);
		recipeDto.setInstructions(instructions);
		recipeDto.setTags(tags);
		recipeDto.setMealType(mealTypes);

		log.info("RecipeMapper :: convertToRecipeDto: Successfully converted RecipeEntity with ID: {}",
				recipeEntity.getId());
		return recipeDto;
	}

}
