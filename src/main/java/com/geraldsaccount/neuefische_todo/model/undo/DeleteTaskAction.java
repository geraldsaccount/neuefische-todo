package com.geraldsaccount.neuefische_todo.model.undo;

import java.util.Optional;

import com.geraldsaccount.neuefische_todo.model.Task;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

public class DeleteTaskAction implements UndoableAction {
	private final TaskRepo repo;
	private final Task task;

	public DeleteTaskAction(TaskRepo repo, Task task) {
		this.repo = repo;
		this.task = task;
	}

	@Override
	public Optional<Task> undo() {
		repo.save(task);
		return Optional.of(task);
	}

	@Override
	public Optional<Task> redo() {
		repo.deleteById(task.id());
		return Optional.empty();
	}

}
