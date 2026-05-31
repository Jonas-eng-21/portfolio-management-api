package br.com.jonassoares.portfolio.domain.exceptions;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
