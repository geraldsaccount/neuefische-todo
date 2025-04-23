package com.geraldsaccount.neuefische_todo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geraldsaccount.neuefische_todo.model.Task;
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
		return tasks.isEmpty()
				? ResponseEntity.noContent().build()
				: ResponseEntity.ok(tasks);
	}
}
