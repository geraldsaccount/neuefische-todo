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

	public Optional<Task> createTask(TaskDTO template) {
		if (template == null || template.description() == null
				|| template.description().isEmpty() || template.description().isBlank()) {
			return Optional.empty();
		}

		Task newTask = Task.of(template)
				.withId(idService.generateId());

		repo.save(newTask);
		return Optional.of(newTask);
	}

	public Optional<Task> getById(String id) {
		return repo.findById(id);
	}

	public Optional<Task> updateTask(String id, Task requestedTask) {
		if (id == null || requestedTask == null
				|| !id.equals(requestedTask.id()) || requestedTask.description() == null
				|| requestedTask.description().isEmpty() || requestedTask.description().isBlank()) {
			return Optional.empty();
		}
		if (!repo.existsById(id)) {
			return Optional.empty();
		}

		repo.save(requestedTask);
		return Optional.of(requestedTask);
	}

	public Boolean delete(String id) {
		if (id == null || id.isEmpty()
				|| !repo.existsById(id)) {
			return false;
		}

		repo.deleteById(id);
		return true;
	}

}
