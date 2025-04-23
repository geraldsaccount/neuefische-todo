package com.geraldsaccount.neuefische_todo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // needed to reset mvc before each test
public class TaskControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getTasks_returnsTasks_whenCalled() throws Exception {
		mvc.perform(post("/api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"description": "this should return",
							"status": "OPEN"
						}
						"""))
				.andExpect(status().isOk());

		mvc.perform(get("/api/todo"))
				.andExpect(status().isOk())
				.andExpect(content().json("""
						[
							{
								"description": "this should return",
								"status": "OPEN"
							}
						]
						"""));
	}

	@Test
	void postTask_returnsTask_withValidDto() throws Exception {
		mvc.perform(post("/api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"description": "this should return",
							"status": "OPEN"
						}
						"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").isNotEmpty());
	}

	@Test
	void postTask_returnsBadRequest_withInvalidDto() throws Exception {
		mvc.perform(post("/api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"status": "OPEN"
						}
						"""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getById_returnsBadRequest_withInvalidId() throws Exception {
		mvc.perform(post("/api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"description": "this should return",
							"status": "OPEN"
						}
						"""))
				.andExpect(status().isOk());

		mvc.perform(get("/api/todo/T1"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getById_returnsTask_withValidId() throws Exception {
		String response = mvc.perform(post("/api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"description": "this should return",
							"status": "OPEN"
						}
						"""))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode node = objectMapper.readTree(response);
		String id = node.get("id").asText();

		mvc.perform(get("/api/todo/" + id))
				.andExpect(status().isOk())
				.andExpect(content().json("""
							{
								"description": "this should return",
								"status": "OPEN"
							}
						"""))
				.andExpect(jsonPath("$.id").value(id));
	}
}
