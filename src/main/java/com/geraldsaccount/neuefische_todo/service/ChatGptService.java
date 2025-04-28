package com.geraldsaccount.neuefische_todo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;

public class ChatGptService {
	private final RestClient client;

	public ChatGptService(RestClient.Builder builder, @Value("${servers.openai.uri}") String baseUrl) {
		client = builder
				.baseUrl(baseUrl)
				.build();
	}

	public String getCorrectedText(String input) {
		return "";
	}
}
