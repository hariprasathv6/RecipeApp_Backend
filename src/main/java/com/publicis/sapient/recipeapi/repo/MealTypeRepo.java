package com.publicis.sapient.recipeapi.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.publicis.sapient.recipeapi.entity.MealTypeEntity;

public interface MealTypeRepo extends JpaRepository<MealTypeEntity, Integer> {

	@Query("SELECT e FROM MealTypeEntity e WHERE e.mealType IN :names")
	List<MealTypeEntity> findExistingMealType(@Param("names") List<String> names);

}
