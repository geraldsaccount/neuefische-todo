package com.geraldsaccount.neuefische_todo.model.dto;

import com.geraldsaccount.neuefische_todo.model.TaskStatus;

public record TaskDTO(String description, TaskStatus status) {

}
