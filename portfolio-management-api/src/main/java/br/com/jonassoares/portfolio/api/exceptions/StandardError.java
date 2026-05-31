package br.com.jonassoares.portfolio.api.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record StandardError(
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
		LocalDateTime timestamp,
		Integer status,
		String error,
		String message,
		String path,
		List<String> details
) {}