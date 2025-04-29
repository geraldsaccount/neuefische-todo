package com.geraldsaccount.neuefische_todo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.geraldsaccount.neuefische_todo.model.error.ErrorDTO;
import com.geraldsaccount.neuefische_todo.model.tasks.Task;
import com.geraldsaccount.neuefische_todo.model.tasks.dto.TaskDTO;
import com.geraldsaccount.neuefische_todo.model.undo.RedoNotPossibleException;
import com.geraldsaccount.neuefische_todo.model.undo.UndoNotPossibleException;
import com.geraldsaccount.neuefische_todo.service.TaskService;
import com.geraldsaccount.neuefische_todo.service.TodoNotFoundException;

@RestController
@RequestMapping("/api/todo")
public class TaskController {
	private final TaskService service;

	public TaskController(TaskService service) {
		this.service = service;
	}

	@GetMapping
	@SuppressWarnings("unused")
	private ResponseEntity<List<Task>> getTasks() {
		List<Task> tasks = service.getTasks();
		return ResponseEntity.ok(tasks);
	}

	@GetMapping("/{id}")
	public Task getById(@PathVariable String id) throws TodoNotFoundException {
		return service.getById(id);
	}

	@PostMapping
	@SuppressWarnings("unused")
	@ResponseStatus(HttpStatus.CREATED)
	private Task postTask(@RequestBody final TaskDTO dto) throws IllegalArgumentException {
		return service.createTask(dto);
	}

	@PutMapping("/{id}")
	public Task putUpdateTask(@PathVariable String id, @RequestBody Task requestedTask)
			throws TodoNotFoundException, IllegalArgumentException {
		return service.updateTask(id, requestedTask);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteTask(@PathVariable String id) throws TodoNotFoundException {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(TodoNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorDTO handleTodoNotFoundException(TodoNotFoundException e) {
		return new ErrorDTO(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@PutMapping("/undo")
	public ResponseEntity<Void> undo() throws UndoNotPossibleException {
		service.undo();
		return ResponseEntity.ok().build();
	}

	@PutMapping("/redo")
	public ResponseEntity<Void> redo() throws RedoNotPossibleException {
		service.redo();
		return ResponseEntity.ok().build();
	}

	@ExceptionHandler(UndoNotPossibleException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDTO handleUndoException(UndoNotPossibleException e) {
		return new ErrorDTO(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(RedoNotPossibleException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDTO handleRedoException(RedoNotPossibleException e) {
		return new ErrorDTO(HttpStatus.BAD_REQUEST, e.getMessage());
	}
}
