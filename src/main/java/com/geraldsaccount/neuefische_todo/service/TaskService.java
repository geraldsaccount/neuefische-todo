package com.geraldsaccount.neuefische_todo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.geraldsaccount.neuefische_todo.model.Task;
import com.geraldsaccount.neuefische_todo.model.dto.TaskDTO;
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
		return repo.findAll();
	}

	public Optional<Task> createTask(TaskDTO dto) {
		return Optional.empty();
	}

}
