package com.geraldsaccount.neuefische_todo.model;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.With;

@With
@Document("tasks")
public record Task(String id, String description, TaskStatus status) {

}
