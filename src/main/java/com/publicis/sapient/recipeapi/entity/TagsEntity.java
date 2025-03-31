package com.publicis.sapient.recipeapi.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_tags")
public class TagsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tagsId;

	@Column(nullable = false, unique = true)
	private String tag;

	public TagsEntity(String tag) {
		this.tag = tag;
	}

}
