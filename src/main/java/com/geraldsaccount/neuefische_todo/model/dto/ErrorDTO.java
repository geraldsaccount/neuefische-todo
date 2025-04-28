package com.geraldsaccount.neuefische_todo.model.dto;

import java.time.Instant;

import org.springframework.http.HttpStatus;

public record ErrorDTO(
		HttpStatus status,
		String message,
		Instant timestamp) {

	public ErrorDTO(HttpStatus status, String message) {
		this(status, message, Instant.now());
	}

}
