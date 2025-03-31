package com.publicis.sapient.recipeapi.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.publicis.sapient.recipeapi.entity.TagsEntity;

public interface TagsRepo extends JpaRepository<TagsEntity, Integer> {

	@Query("SELECT e FROM TagsEntity e WHERE e.tag IN :names")
	List<TagsEntity> findExistingTag(@Param("names") List<String> names);
}
