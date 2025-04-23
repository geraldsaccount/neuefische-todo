package com.geraldsaccount.neuefische_todo.service;

import org.springframework.stereotype.Service;

import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

@Service
public class TaskService {
	private final TaskRepo repo;
	private final IdService idService;

	public TaskService(TaskRepo repo, IdService idService) {
		this.repo = repo;
		this.idService = idService;
	}

}
