package com.geraldsaccount.neuefische_todo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.geraldsaccount.neuefische_todo.model.Task;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

@Service
public class TaskService {
	private final TaskRepo repo;
	private final IdService idService;

	public TaskService(TaskRepo repo, IdService idService) {
		this.repo = repo;
		this.idService = idService;
	}

	public List<Task> getTasks() {
		return null;
	}

}
