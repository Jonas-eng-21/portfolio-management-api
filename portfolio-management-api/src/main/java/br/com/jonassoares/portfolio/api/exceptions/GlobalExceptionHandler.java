package br.com.jonassoares.portfolio.api.exceptions;

import br.com.jonassoares.portfolio.domain.exceptions.BusinessRuleException;
import br.com.jonassoares.portfolio.domain.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardError> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
		List<String> details = ex.getBindingResult().getFieldErrors()
				.stream()
				.map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());
		
		StandardError error = new StandardError(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				"Validation Error",
				"One or more fields are invalid",
				request.getRequestURI(),
				details
		);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<StandardError> handleBusinessRule(BusinessRuleException ex, HttpServletRequest request) {
		StandardError error = new StandardError(
				LocalDateTime.now(),
				HttpStatus.UNPROCESSABLE_ENTITY.value(),
				"Broken Business Rule",
				ex.getMessage(),
				request.getRequestURI(),
				List.of()
		);
		
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardError> handleGenericException(Exception ex, HttpServletRequest request) {
		StandardError error = new StandardError(
				LocalDateTime.now(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Internal Server Error",
				ex.getMessage(),
				request.getRequestURI(),
				List.of()
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
		StandardError error = new StandardError(
				LocalDateTime.now(),
				HttpStatus.NOT_FOUND.value(),
				"Resource Not Found",
				ex.getMessage(),
				request.getRequestURI(),
				List.of()
		);
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
}
