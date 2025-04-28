package com.geraldsaccount.neuefische_todo.model.tasks.dto;

import com.geraldsaccount.neuefische_todo.model.tasks.TaskStatus;

public record TaskDTO(String description, TaskStatus status) {

}
