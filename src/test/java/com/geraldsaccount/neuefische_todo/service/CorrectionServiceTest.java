package com.geraldsaccount.neuefische_todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiException;
import com.geraldsaccount.neuefische_todo.model.openai.OpenAiResponse;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

@SpringBootTest(properties = {
		"servers.openai.url=http://localhost:8089"
})
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8089)
public class CorrectionServiceTest {
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private CorrectionService service;

	@Test
	void getCorrectedText_returnsCorrection_withInput() throws JsonProcessingException, OpenAiException {
		String misspelledText = "i are not hungri. me olredi eat.";
		String correctedText = "I am not hungry. I already ate.";
		OpenAiResponse response = OpenAiResponse.ofResponse(correctedText);

		WireMock.stubFor(WireMock.post(CorrectionService.REQUEST_URI)
				.willReturn(WireMock.ok()
						.withHeader("Content-Type", "application/json")
						.withBody(mapper.writeValueAsString(response))));

		assertThat(service.getCorrectedText(misspelledText))
				.isEqualTo(correctedText);
	}
}
