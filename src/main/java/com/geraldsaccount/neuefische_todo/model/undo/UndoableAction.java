package com.geraldsaccount.neuefische_todo.model.undo;

import java.util.Optional;

import com.geraldsaccount.neuefische_todo.model.tasks.Task;

public interface UndoableAction {
	Optional<Task> undo();

	Optional<Task> redo();
}
