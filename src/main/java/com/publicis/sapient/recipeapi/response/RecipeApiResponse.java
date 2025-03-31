package com.publicis.sapient.recipeapi.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
public class RecipeApiResponse {
	private Integer status;
	private String message;
	private LocalDateTime time;
	private Object data;
	private Map<String, String> fieldErrors;

	public RecipeApiResponse(Integer status, String message, LocalDateTime time, Object data) {
		this.status = status;
		this.message = message;
		this.time = time;
		this.data = data;
	}

}
