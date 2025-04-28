package com.geraldsaccount.neuefische_todo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.geraldsaccount.neuefische_todo.model.openai.OpenAiException;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiOutput;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiRequest;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiResponse;

@Service
public class ChatGptService {
	private final RestClient client;

	@Value("${servers.openai.uri}")
	private String requestUri;

	public ChatGptService(RestClient.Builder builder,
			@Value("${servers.openai.url}") String baseUrl,
			@Value("${servers.openai.key}") String key) {
		client = builder
				.baseUrl(baseUrl)
				.defaultHeader("Authorization", "Bearer " + key)
				.build();
	}

	public String getCorrectedText(String input) throws OpenAiException {
		OpenAiRequest request = new OpenAiRequest("gpt-4.1", "Correct the following text for spelling and grammar.",
				input);
		OpenAiResponse response = client.post()
				.uri(requestUri)
				.contentType(MediaType.APPLICATION_JSON)
				.body(request)
				.retrieve()
				.body(new ParameterizedTypeReference<>() {
				});
		if (response == null) {
			throw new OpenAiException("Did not retrieve response from OpenAI");
		}

		if (!response.status().equals("completed")
				|| response.output() == null || response.output().isEmpty()) {
			throw new OpenAiException(response.error());
		}

		OpenAiOutput output = response.output().getFirst();
		if (!output.status().equals("completed") ||
				output.content() == null || output.content().isEmpty()) {
			throw new OpenAiException("OpenAi response output was not completed.");
		}

		return output.content().getFirst().text();
	}
}
