package com.geraldsaccount.neuefische_todo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.geraldsaccount.neuefische_todo.service.TaskService;

@RestController
@RequestMapping("/api/todo")
public class TaskController {
	private final TaskService service;

	public TaskController(TaskService service) {
		this.service = service;
	}

}
