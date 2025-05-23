package com.geraldsaccount.neuefische_todo.model.tasks;

import org.springframework.data.mongodb.core.mapping.Document;

import com.geraldsaccount.neuefische_todo.model.tasks.dto.TaskDTO;

import lombok.With;

@With
@Document("tasks")
public record Task(String id, String description, TaskStatus status) {
	public static Task of(TaskDTO dto) {
		return new Task("", dto.description(), dto.status());
	}
}
