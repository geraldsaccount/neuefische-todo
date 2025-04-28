package com.geraldsaccount.neuefische_todo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiContent;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiOutput;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiResponse;
import com.github.tomakehurst.wiremock.client.WireMock;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatGptServiceTest {

	@Value("${servers.openai.uri}")
	private String url;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ChatGptService service;

	@Test
	void getCorrectedText_returnsCorrection_withInput() throws JsonProcessingException {
		String misspelledText = "i are not hungri. me olredi eat.";
		String correctedText = "I am not hungry. I already ate.";
		OpenAiResponse response = new OpenAiResponse(
				"completed",
				"Correct the following text's spelling and grammar.",
				List.of(new OpenAiOutput(
						"completed",
						List.of(
								new OpenAiContent("output_text", correctedText)))));
		WireMock.stubFor(WireMock.post(url)
				.willReturn(WireMock.ok()
						.withHeader("Content-Type", "application/json")
						.withBody(mapper.writeValueAsString(response))));

		assertThat(service.getCorrectedText(misspelledText))
				.isEqualTo(correctedText);
	}
}
