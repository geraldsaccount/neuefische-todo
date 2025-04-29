package com.geraldsaccount.neuefische_todo.model.undo;

import java.util.Optional;

import com.geraldsaccount.neuefische_todo.model.tasks.Task;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

public class UpdateTaskAction implements UndoableAction {
	private final TaskRepo repo;
	private final Task before;
	private final Task after;

	public UpdateTaskAction(TaskRepo repo, Task after, Task before) {
		this.repo = repo;
		this.after = after;
		this.before = before;
	}

	@Override
	public Optional<Task> undo() {
		repo.save(before);
		return Optional.of(before);
	}

	@Override
	public Optional<Task> redo() {
		repo.save(after);
		return Optional.of(after);
	}

}
