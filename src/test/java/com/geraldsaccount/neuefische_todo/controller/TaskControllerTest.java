package com.geraldsaccount.neuefische_todo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

	@Autowired
	private static MockMvc mvc;

	@Test
	void getTasks_returnsTasks_whenCalled() throws Exception {
		mvc.perform(post("api/todo")
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
		mvc.perform(post("api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"description": "this should return",
							"status": "OPEN"
						}
						"""))
				.andExpect(status().isOk());
	}

	@Test
	void postTask_returnsBadRequest_withInvalidDto() throws Exception {
		mvc.perform(post("api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"status": "OPEN"
						}
						"""))
				.andExpect(status().isBadRequest());
	}
}
