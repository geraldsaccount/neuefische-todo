package com.geraldsaccount.neuefische_todo.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiContent;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiException;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiOutput;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiResponse;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@SpringBootTest(properties = {
		"servers.openai.url=http://localhost:8089"
})
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8089)
public class ChatGptServiceTest {

	@Value("${servers.openai.uri}")
	private String uri;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ChatGptService service;

	@Test
	void getCorrectedText_returnsCorrection_withInput() throws JsonProcessingException, OpenAiException {
		String misspelledText = "i are not hungri. me olredi eat.";
		String correctedText = "I am not hungry. I already ate.";
		OpenAiResponse response = new OpenAiResponse(
				"completed",
				"Correct the following text's spelling and grammar.",
				List.of(new OpenAiOutput(
						"completed",
						List.of(
								new OpenAiContent("output_text", correctedText)))));
		WireMock.stubFor(WireMock.post(uri)
				.willReturn(WireMock.ok()
						.withHeader("Content-Type", "application/json")
						.withBody(mapper.writeValueAsString(response))));

		assertThat(service.getCorrectedText(misspelledText))
				.isEqualTo(correctedText);
	}
}
