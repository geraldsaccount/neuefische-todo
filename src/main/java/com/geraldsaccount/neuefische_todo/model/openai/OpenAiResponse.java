package com.geraldsaccount.neuefische_todo.model.openai;

import java.util.List;

public record OpenAiResponse(String id,
		String object,
		long createdAt,
		String status,
		String error,
		String incompleteDetails,
		String instructions,
		List<OpenAiOutput> output) {
	public OpenAiResponse(String status, String instructions, List<OpenAiOutput> output) {
		this("O1", "object", 1, status, null, null, instructions, output);
	}
}
