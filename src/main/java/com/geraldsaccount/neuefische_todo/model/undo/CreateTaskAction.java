package com.geraldsaccount.neuefische_todo.model.undo;

import java.util.Optional;

import com.geraldsaccount.neuefische_todo.model.tasks.Task;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

public class CreateTaskAction implements UndoableAction {
	private final TaskRepo repo;
	private final Task task;

	public CreateTaskAction(TaskRepo repo, Task task) {
		this.repo = repo;
		this.task = task;
	}

	@Override
	public Optional<Task> undo() {
		repo.deleteById(task.id());
		return Optional.empty();
	}

	@Override
	public Optional<Task> redo() {
		repo.save(task);
		return Optional.of(task);
	}

}
