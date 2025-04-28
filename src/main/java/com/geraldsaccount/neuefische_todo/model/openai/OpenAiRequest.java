package com.geraldsaccount.neuefische_todo.model.openai;

public record OpenAiRequest(
		String model,
		String instructions,
		String input) {

}
