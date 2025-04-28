package com.geraldsaccount.neuefische_todo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.geraldsaccount.neuefische_todo.model.openai.OpenAiException;
import com.geraldsaccount.neuefische_todo.model.tasks.Task;
import com.geraldsaccount.neuefische_todo.model.tasks.dto.TaskDTO;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

@Service
public class TaskService {
	private final TaskRepo repo;
	private final IdService idService;
	private final CorrectionService correctionService;

	public TaskService(TaskRepo repo, IdService idService, CorrectionService chatService) {
		this.repo = repo;
		this.idService = idService;
		this.correctionService = chatService;
	}

	public List<Task> getTasks() {
		return repo.findAll();
	}

	public Task createTask(TaskDTO template) {
		if (template == null || template.description() == null
				|| template.description().isEmpty() || template.description().isBlank()) {
			throw new IllegalArgumentException("Cannot create todo. Missing informations.");
		}

		Task newTask = Task.of(template)
				.withDescription(getCorrection(template.description()))
				.withId(idService.generateId());

		repo.save(newTask);
		return newTask;
	}

	public Task getById(String id) throws TodoNotFoundException {
		return repo.findById(id)
				.orElseThrow(() -> new TodoNotFoundException("Todo with id " + id + "was not found."));
	}

	public Task updateTask(String id, Task requestedTask) throws TodoNotFoundException {
		if (id == null || requestedTask == null
				|| !id.equals(requestedTask.id()) || requestedTask.description() == null
				|| requestedTask.description().isEmpty() || requestedTask.description().isBlank()) {
			throw new IllegalArgumentException("Cannot update todo. Missing informations.");
		}
		if (!repo.existsById(id)) {
			throw new TodoNotFoundException("Todo with id " + id + "was not found.");
		}

		repo.save(requestedTask
				.withDescription(getCorrection(requestedTask.description())));
		return requestedTask;
	}

	public void delete(String id) throws TodoNotFoundException {
		if (id == null || id.isEmpty()
				|| !repo.existsById(id)) {
			throw new TodoNotFoundException("Todo with id " + id + "was not found.");
		}

		repo.deleteById(id);
	}

	private String getCorrection(String input) {
		String correctedDescription = input;
		try {
			correctedDescription = correctionService
					.getCorrectedText(correctedDescription);

		} catch (OpenAiException e) {

		}
		return correctedDescription;
	}
}
