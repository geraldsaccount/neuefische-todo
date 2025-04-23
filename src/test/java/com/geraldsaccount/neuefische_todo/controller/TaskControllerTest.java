package com.geraldsaccount.neuefische_todo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
	void getById_returnsNotFound_withInvalidId() throws Exception {
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
				.andExpect(status().isNotFound());
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

	@Test
	void putTask_updatesTask_withValidData() throws Exception {
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

		mvc.perform(put("/api/todo/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"id": "temp",
							"description": "updated text",
							"status": "DOING"
						}
						""".replaceFirst("temp", id)))
				.andExpect(status().isOk())
				.andExpect(content().json("""
							{
								"description": "updated text",
								"status": "DOING"
							}
						"""))
				.andExpect(jsonPath("$.id").value(id));
	}

	@Test
	void putTask_returnsBadRequest_withMissingDescription() throws Exception {
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

		mvc.perform(put("/api/todo/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"id": "temp",
							"description": "",
							"status": DOING
						}
						""".replaceFirst("temp", id)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void putTask_returnsBadRequest_withMismatchingIds() throws Exception {
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

		mvc.perform(put("/api/todo/" + id)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"id": "T1",
							"description": "updated text",
							"status": DOING
						}
						"""))
				.andExpect(status().isBadRequest());
	}

	@Test
	void putTask_returnsNotFound_withInvalidID() throws Exception {
		mvc.perform(post("/api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"description": "this should return",
							"status": "OPEN"
						}
						"""))
				.andExpect(status().isOk());

		String invalidId = "T1";

		mvc.perform(put("/api/todo/" + invalidId)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"id": "temp",
							"description": "",
							"status": DOING
						}
						""".replaceFirst("temp", invalidId)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deleteTask_returnsOk_withValidId() throws Exception {
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

		mvc.perform(delete("/api/todo/" + id))
				.andExpect(status().isOk());
	}

	@Test
	void deleteTask_returnsNotFound_withInvalidId() throws Exception {
		mvc.perform(delete("/api/todo/T1"))
				.andExpect(status().isNotFound());
	}
}
