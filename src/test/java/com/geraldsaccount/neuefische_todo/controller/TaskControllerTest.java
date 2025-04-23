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

}
