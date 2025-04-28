package com.geraldsaccount.neuefische_todo.model.openai;

import java.util.List;

public record OpenAiOutput(String id,
		String type,
		String status,
		List<OpenAiContent> content,
		String role) {
	public OpenAiOutput(String status, List<OpenAiContent> content) {
		this("id", "type", status, content, "role");
	}
}
