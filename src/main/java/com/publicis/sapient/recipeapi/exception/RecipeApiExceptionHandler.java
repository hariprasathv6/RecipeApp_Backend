package com.publicis.sapient.recipeapi.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.boot.MappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.publicis.sapient.recipeapi.response.RecipeApiResponse;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class RecipeApiExceptionHandler {

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<RecipeApiResponse> handleRecipeNFException(RecipeNotFoundException e) {
    	log.warn("RecipeAppExceptionHandler::handleRecipeNFException: Recipe not found: {}", e.getMessage());
        RecipeApiResponse response = new RecipeApiResponse();
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setMessage(e.getMessage());
		response.setTime(LocalDateTime.now());
        log.warn("RecipeAppExceptionHandler :: handleRecipeNFException :: response : {}", response);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<RecipeApiResponse> handleInvalidInputException(InvalidInputException e) {
        log.warn("RecipeAppExceptionHandler::handleInvalidInputException: {}", e.getMessage());
        RecipeApiResponse response = new RecipeApiResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(), LocalDateTime.now(), null);
        log.warn("RecipeAppExceptionHandler :: handleInvalidInputException :: response : {}", response);
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<RecipeApiResponse> handleIllegalStateException(Exception e) {
        log.warn("RecipeAppExceptionHandler::handleIllegalStateException: {}", e.getMessage());
        RecipeApiResponse response = new RecipeApiResponse(HttpStatus.NOT_FOUND.value(),
                e.getMessage(), LocalDateTime.now(),null);
        log.warn("RecipeAppExceptionHandler :: handleIllegalStateException :: response : {}", response);

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.warn("RecipeAppExceptionHandler::handleValidationErrors: {}", ex.getMessage());
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("errorCode", "ERR-VALIDATION-001");
        errorResponse.put("message", "Validation error: One or more fields are invalid.");
        errorResponse.put("errors", fieldErrors);
        log.warn("RecipeAppExceptionHandler::handleValidationErrors :: response : {}", errorResponse);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RecipeApiResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("RecipeAppExceptionHandler::MethodArgumentTypeMismatchException: {}", e.getMessage());
        RecipeApiResponse response = new RecipeApiResponse();
        response.setMessage("value for parameter" + " " + e.getName()
                + "Not valid. Please provide a valid positive whole number (like 1, 2, 3, 10)");
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTime(LocalDateTime.now());
        log.warn("RecipeAppExceptionHandler:: handleTypeMismatchException ::response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<RecipeApiResponse> handleMissingParams(MissingServletRequestParameterException e) {
        log.warn("RecipeAppExceptionHandler::handleMissingParams: {}", e.getMessage());
        RecipeApiResponse response = new RecipeApiResponse();
        response.setMessage("Missing required parameter value for " + e.getParameterName());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setTime(LocalDateTime.now());
        log.warn("RecipeAppExceptionHandler::handleMissingParams :: response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<RecipeApiResponse> handlingDBException(DatabaseException e) {
        log.error("RecipeAppExceptionHandler::handlingDBException: {}", e.getMessage(),e);
        RecipeApiResponse response = new RecipeApiResponse();
        response.setMessage(e.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setTime(LocalDateTime.now());
        log.warn("RecipeAppExceptionHandler::handlingDBException :: response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<RecipeApiResponse> handleHttpClientError(HttpClientErrorException e) {
        log.error("RecipeAppExceptionHandler:: handleHttpClientError :: HTTP Error: Status {} - {}", e.getStatusCode(),
                e.getMessage(),e);
        RecipeApiResponse response = new RecipeApiResponse();
        response.setMessage("Failed to fetch data from external API. Please try again later");
        response.setStatus(e.getStatusCode().value());
        response.setTime(LocalDateTime.now());
        log.error("RecipeAppExceptionHandler::handleHttpClientError :: response: {}", response);
        return new ResponseEntity<>(response, e.getStatusCode());
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<RecipeApiResponse> handleRestClientException(RestClientException e) {
        log.error("RecipeAppExceptionHandler:: handleRestClientException :: External API error: {}", e.getMessage(), e);
        RecipeApiResponse response = new RecipeApiResponse();
        response.setMessage("Failed to fetch data from external API. Please try again later");
        response.setStatus(HttpStatus.BAD_GATEWAY.value());
        response.setTime(LocalDateTime.now());
        log.error("RecipeAppExceptionHandler:: handleRestClientException ::response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<RecipeApiResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("RecipeAppExceptionHandler:: handleEntityNotFoundException ::Resource not found: {}", e.getMessage(), e);
        RecipeApiResponse response = new RecipeApiResponse();
        response.setMessage("Resource not found:" + e.getMessage());
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setTime(LocalDateTime.now());
        log.error("RecipeAppExceptionHandler:: handleEntityNotFoundException ::response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MappingException.class)
    public ResponseEntity<RecipeApiResponse> hanldeMappingException(MappingException e) {
        log.error("RecipeAppExceptionHandler:: hanldeMappingException :: MappingException occurred: {}", e.getMessage(), e);
        RecipeApiResponse response = new RecipeApiResponse();
        response.setMessage(e.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setTime(LocalDateTime.now());
        log.error("RecipeAppExceptionHandler:: hanldeMappingException ::response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RecipeApiResponse> handleRuntimeException(RuntimeException e) {
        log.error("RecipeAppExceptionHandler:: handleRuntimeException :: RuntimeException: {}", e.getMessage(), e);
        RecipeApiResponse response = new RecipeApiResponse();
        response.setMessage(e.getMessage());
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setTime(LocalDateTime.now());
        log.error("RecipeAppExceptionHandler:: handleRuntimeException ::response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<RecipeApiResponse> handleResponseStatusException(ResponseStatusException e) {
        log.error("RecipeAppExceptionHandler:: handleResponseStatusException :: {}", e.getReason(), e);
        RecipeApiResponse response = new RecipeApiResponse();
        response.setMessage(e.getReason());
        response.setStatus(e.getStatusCode().value());
        response.setTime(LocalDateTime.now());
        log.error("RecipeAppExceptionHandler:: handleResponseStatusException ::response: {}", response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RecipeApiResponse> handleGeneralException(Exception e) {
        log.error("RecipeApiExceptionHandler::handleGeneralException::Unexpected error: {}", e.getMessage(), e);
        RecipeApiResponse response = new RecipeApiResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please try again later.",
                LocalDateTime.now(),
                null
        );
        log.error("RecipeAppExceptionHandler:: handleGeneralException ::response: {}", response);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}