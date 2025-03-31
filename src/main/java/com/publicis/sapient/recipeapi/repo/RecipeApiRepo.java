package com.publicis.sapient.recipeapi.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.publicis.sapient.recipeapi.entity.IngredientsEntity;
import com.publicis.sapient.recipeapi.entity.RecipeEntity;

public interface RecipeApiRepo extends JpaRepository<RecipeEntity, Integer> {
	
}

