package com.geraldsaccount.neuefische_todo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.geraldsaccount.neuefische_todo.model.Task;
import com.geraldsaccount.neuefische_todo.model.dto.TaskDTO;
import com.geraldsaccount.neuefische_todo.model.undo.CreateTaskAction;
import com.geraldsaccount.neuefische_todo.model.undo.DeleteTaskAction;
import com.geraldsaccount.neuefische_todo.model.undo.UndoableAction;
import com.geraldsaccount.neuefische_todo.model.undo.UpdateTaskAction;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

@Service
public class TaskService {
	private final TaskRepo repo;
	private final IdService idService;

	private List<UndoableAction> commandHistory;
	private int currentActionIndex;

	public TaskService(TaskRepo repo, IdService idService) {
		this.repo = repo;
		this.idService = idService;
		commandHistory = new ArrayList<>();
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

		addCommand(new CreateTaskAction(repo, newTask));

		return Optional.of(newTask);
	}

	public Optional<Task> getById(String id) {
		return repo.findById(id);
	}

	public Optional<Task> updateTask(String id, Task requestedTask) {
		if (id == null || requestedTask == null ||
				!id.equals(requestedTask.id()) || requestedTask.description() == null) {
			return Optional.empty();
		}

		Optional<Task> foundTask = repo.findById(id);
		if (foundTask.isEmpty()) {
			return foundTask;
		}

		Task beforeTask = foundTask.get();
		addCommand(new UpdateTaskAction(repo, requestedTask, beforeTask));

		repo.save(requestedTask);
		return Optional.of(requestedTask);
	}

	public Boolean delete(String id) {

		if (id == null || id.isEmpty()) {
			return false;
		}

		Optional<Task> toDelete = repo.findById(id);
		if (toDelete.isEmpty()) {
			return false;
		}

		addCommand(new DeleteTaskAction(repo, toDelete.get()));

		repo.deleteById(id);
		return true;
	}

	private void addCommand(UndoableAction command) {
		if (currentActionIndex == commandHistory.size()) {
			commandHistory.add(command);
			currentActionIndex = commandHistory.size();
			return;
		}

		commandHistory = new ArrayList<>(commandHistory.stream().limit(currentActionIndex).toList());
		commandHistory.add(command);
		currentActionIndex = commandHistory.size();
	}

	public boolean undo() {
		if (currentActionIndex == 0) {
			return false;
		}
		currentActionIndex--;
		commandHistory.get(currentActionIndex).undo();
		return true;
	}

	public boolean redo() {
		if (currentActionIndex >= commandHistory.size()) {
			return false;
		}
		commandHistory.get(currentActionIndex).redo();
		currentActionIndex++;
		return true;
	}

}
