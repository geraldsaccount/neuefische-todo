package com.geraldsaccount.neuefische_todo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class UUIDService implements IdService {

	@Override
	public String generateId() {
		return UUID.randomUUID().toString();
	}

}
