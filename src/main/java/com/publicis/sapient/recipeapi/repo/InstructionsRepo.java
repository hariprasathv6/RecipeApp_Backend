package com.publicis.sapient.recipeapi.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.publicis.sapient.recipeapi.entity.IngredientsEntity;
import com.publicis.sapient.recipeapi.entity.InstructionsEntity;

public interface InstructionsRepo extends JpaRepository<InstructionsEntity, Integer> {

	@Query("SELECT e FROM InstructionsEntity e WHERE e.instruction IN :names")
	List<InstructionsEntity> findExistingInstruction(@Param("names") List<String> names);
}
