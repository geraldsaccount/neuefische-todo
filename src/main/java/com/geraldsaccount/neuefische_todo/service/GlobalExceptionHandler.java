package com.geraldsaccount.neuefische_todo.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.geraldsaccount.neuefische_todo.model.error.ErrorDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDTO handleIllegalArgumentException(IllegalArgumentException e) {
		return new ErrorDTO(HttpStatus.BAD_REQUEST, e.getMessage());
	}
}
