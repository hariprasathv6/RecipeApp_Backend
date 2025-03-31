package com.publicis.sapient.recipeapi.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_ingredients", uniqueConstraints = @UniqueConstraint(columnNames = "ingredientsname"))
public class IngredientsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer ingredientsid;

	private String ingredientsname;

	public IngredientsEntity(String ingredientsname) {
		this.ingredientsname = ingredientsname;
	}

}
