package com.geraldsaccount.neuefische_todo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.geraldsaccount.neuefische_todo.model.openai.OpenAiException;
import com.geraldsaccount.neuefische_todo.model.tasks.Task;
import com.geraldsaccount.neuefische_todo.model.tasks.dto.TaskDTO;
import com.geraldsaccount.neuefische_todo.model.undo.CreateTaskAction;
import com.geraldsaccount.neuefische_todo.model.undo.DeleteTaskAction;
import com.geraldsaccount.neuefische_todo.model.undo.RedoNotPossibleException;
import com.geraldsaccount.neuefische_todo.model.undo.UndoNotPossibleException;
import com.geraldsaccount.neuefische_todo.model.undo.UndoableAction;
import com.geraldsaccount.neuefische_todo.model.undo.UpdateTaskAction;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

@Service
public class TaskService {
	private final TaskRepo repo;
	private final IdService idService;
	private final CorrectionService correctionService;

	private List<UndoableAction> commandHistory;
	private int currentActionIndex;

	public TaskService(TaskRepo repo, IdService idService, CorrectionService chatService) {
		this.repo = repo;
		this.idService = idService;
		this.correctionService = chatService;
		commandHistory = new ArrayList<>();
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

		addCommand(new CreateTaskAction(repo, newTask));

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

		Optional<Task> foundTask = repo.findById(id);
		if (foundTask.isEmpty()) {
			throw new TodoNotFoundException("Todo with id " + id + "was not found.");
		}

		Task beforeTask = foundTask.get();
		addCommand(new UpdateTaskAction(repo, requestedTask, beforeTask));
		repo.save(requestedTask
				.withDescription(getCorrection(requestedTask.description())));
		return requestedTask;
	}

	public void delete(String id) throws TodoNotFoundException {
		Optional<Task> toDelete = repo.findById(id);
		if (id == null || id.isEmpty()
				|| toDelete.isEmpty()) {
			throw new TodoNotFoundException("Todo with id " + id + "was not found.");
		}

		addCommand(new DeleteTaskAction(repo, toDelete.get()));

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

	public void addCommand(UndoableAction command) {
		if (currentActionIndex == commandHistory.size()) {
			commandHistory.add(command);
			currentActionIndex = commandHistory.size();
			return;
		}

		commandHistory = new ArrayList<>(commandHistory.stream().limit(currentActionIndex).toList());
		commandHistory.add(command);
		currentActionIndex = commandHistory.size();
	}

	public void undo() throws UndoNotPossibleException {
		if (currentActionIndex == 0) {
			throw new UndoNotPossibleException("No action left to undo.");
		}
		currentActionIndex--;
		commandHistory.get(currentActionIndex).undo();
	}

	public void redo() throws RedoNotPossibleException {
		if (currentActionIndex >= commandHistory.size()) {
			throw new RedoNotPossibleException("No action left to redo.");
		}
		commandHistory.get(currentActionIndex).redo();
		currentActionIndex++;
	}

}
