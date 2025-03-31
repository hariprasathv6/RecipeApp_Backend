package com.publicis.sapient.recipeapi.exception;

import com.publicis.sapient.recipeapi.response.RecipeApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.hibernate.boot.MappingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RecipeApiExceptionHandlerTest {

	@InjectMocks
	private RecipeApiExceptionHandler exceptionHandler;

	@Mock
	private BindingResult bindingResult;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void handleRecipeNFException() {
		RecipeNotFoundException exception = new RecipeNotFoundException("Recipe not found");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleRecipeNFException(exception);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Recipe not found", response.getBody().getMessage());
	}

	@Test
	void handleInvalidInputException() {
		InvalidInputException exception = new InvalidInputException("Invalid input");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleInvalidInputException(exception);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Invalid input", response.getBody().getMessage());
	}

	@Test
	void handleIllegalStateException() {
		IllegalStateException exception = new IllegalStateException("Illegal state");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleIllegalStateException(exception);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Illegal state", response.getBody().getMessage());
	}

	@Test
	void handleValidationErrors() {
		MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
		when(exception.getBindingResult()).thenReturn(bindingResult);
		FieldError fieldError = new FieldError("objectName", "fieldName", "errorMessage");
		when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));
		ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidationErrors(exception);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		Map<String, Object> body = response.getBody();
		assertNotNull(body);
		assertEquals("Validation error: One or more fields are invalid.", body.get("message"));
	}

	@Test
	void handleTypeMismatchException() {
		MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
		when(exception.getName()).thenReturn("paramName");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleTypeMismatchException(exception);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals(
				"value for parameter paramNameNot valid. Please provide a valid positive whole number (like 1, 2, 3, 10)",
				response.getBody().getMessage());
	}

	@Test
	void handleMissingParams() {
		MissingServletRequestParameterException exception = mock(MissingServletRequestParameterException.class);
		when(exception.getParameterName()).thenReturn("paramName");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleMissingParams(exception);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Missing required parameter value for paramName", response.getBody().getMessage());
	}

	@Test
	void handlingDBException() {
		DatabaseException exception = new DatabaseException("DB error");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handlingDBException(exception);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("DB error", response.getBody().getMessage());
	}

	@Test
	void handleHttpClientError() {
		HttpClientErrorException exception = mock(HttpClientErrorException.class);
		when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
		when(exception.getMessage()).thenReturn("HTTP error");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleHttpClientError(exception);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("Failed to fetch data from external API. Please try again later", response.getBody().getMessage());
	}

	@Test
	void handleRestClientException() {
		RestClientException exception = new RestClientException("Rest client error");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleRestClientException(exception);
		assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
		assertEquals("Failed to fetch data from external API. Please try again later", response.getBody().getMessage());
	}

	@Test
	void handleEntityNotFoundException() {
		EntityNotFoundException exception = new EntityNotFoundException("Entity not found");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleEntityNotFoundException(exception);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("Resource not found:Entity not found", response.getBody().getMessage());
	}

	@Test
	void hanldeMappingException() {
		MappingException exception = new MappingException("Mapping error", null);
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.hanldeMappingException(exception);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Mapping error", response.getBody().getMessage());
	}

	@Test
	void handleRuntimeException() {
		RuntimeException exception = new RuntimeException("Runtime error");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleRuntimeException(exception);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("Runtime error", response.getBody().getMessage());
	}

	@Test
	void handleResponseStatusException() {
		ResponseStatusException exception = new ResponseStatusException(HttpStatus.OK, "Response status error");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleResponseStatusException(exception);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Response status error", exception.getReason());
	}

	@Test
	void handleGeneralException() {
		Exception exception = new Exception("General error");
		ResponseEntity<RecipeApiResponse> response = exceptionHandler.handleGeneralException(exception);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
		assertEquals("An unexpected error occurred. Please try again later.", response.getBody().getMessage());
	}
}