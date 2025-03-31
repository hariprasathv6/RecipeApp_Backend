package com.publicis.sapient.recipeapi.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.publicis.sapient.recipeapi.entity.IngredientsEntity;

public interface IngredientsRepo extends JpaRepository<IngredientsEntity, Integer> {

	@Query("SELECT e FROM IngredientsEntity e WHERE e.ingredientsname IN :names")
	List<IngredientsEntity> findExistingIngredients(@Param("names") List<String> names);
}
