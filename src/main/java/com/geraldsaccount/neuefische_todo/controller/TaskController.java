package com.geraldsaccount.neuefische_todo.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geraldsaccount.neuefische_todo.model.Task;
import com.geraldsaccount.neuefische_todo.model.dto.TaskDTO;
import com.geraldsaccount.neuefische_todo.service.TaskService;

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
	public ResponseEntity<Task> getById(@PathVariable String id) {
		return service.getById(id)
				.map(t -> ResponseEntity.ok(t))
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	@SuppressWarnings("unused")
	private ResponseEntity<Task> postTask(@RequestBody final TaskDTO dto) {
		return service.createTask(dto)
				.map(t -> ResponseEntity
						.created(URI.create("/api/todo/" + t.id()))
						.body(t))
				.orElse(ResponseEntity.badRequest().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Task> putUpdateTask(@PathVariable String id, @RequestBody Task requestedTask) {
		return service.updateTask(id, requestedTask)
				.map(t -> ResponseEntity.ok(t))
				.orElse(ResponseEntity.badRequest().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Boolean> deleteTask(@PathVariable String id) {
		return service.delete(id)
				? ResponseEntity.noContent().build() // 204 No Content
				: ResponseEntity.notFound().build(); // 404 Not Found
	}

	@GetMapping("/undo")
	public ResponseEntity<Void> undo() {
		return service.undo()
				? ResponseEntity.accepted().build()
				: ResponseEntity.noContent().build();
	}

	@GetMapping("/redo")
	public ResponseEntity<Void> redo() {
		return service.redo()
				? ResponseEntity.accepted().build()
				: ResponseEntity.noContent().build();
	}

}
