package com.geraldsaccount.neuefische_todo.controller;

import java.util.List;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geraldsaccount.neuefische_todo.model.Task;
import com.geraldsaccount.neuefische_todo.model.TaskStatus;
import com.geraldsaccount.neuefische_todo.model.dto.TaskDTO;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // needed to reset mvc before each test
public class TaskControllerTest {

	@Autowired
	private TaskRepo repo;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void getTasks_returnsTasks_whenCalled() throws Exception {
		Task task = new Task("T1", "this should return", TaskStatus.DONE);
		repo.save(task);

		mvc.perform(get("/api/todo"))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(List.of(task))));
	}

	@Test
	void postTask_returnsTask_withValidDto() throws Exception {
		TaskDTO dto = new TaskDTO("this should become a task", TaskStatus.IN_PROGRESS);
		String jsonDto = objectMapper.writeValueAsString(dto);

		mvc.perform(post("/api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonDto))
				.andExpect(status().isCreated())
				.andExpect(content().json(jsonDto))
				.andExpect(jsonPath("$.id").isNotEmpty());
	}

	@Test
	void postTask_returnsBadRequest_withInvalidDto() throws Exception {
		TaskDTO dto = new TaskDTO(null, TaskStatus.IN_PROGRESS);

		mvc.perform(post("/api/todo")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getById_returnsNotFound_withInvalidId() throws Exception {
		Task task = new Task("T1", "should not be found", TaskStatus.OPEN);
		repo.save(task);

		mvc.perform(get("/api/todo/T2"))
				.andExpect(status().isNotFound());
	}

	@Test
	void getById_returnsTask_withValidId() throws Exception {
		Task task = new Task("T1", "should be found", TaskStatus.OPEN);
		repo.save(task);

		mvc.perform(get("/api/todo/" + task.id()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(task)));
	}

	@Test
	void putTask_updatesTask_withValidData() throws Exception {
		Task task = new Task("T1", "text before", TaskStatus.OPEN);
		repo.save(task);

		Task updatedTask = task
				.withDescription("updated text")
				.withStatus(TaskStatus.DONE);

		String jsonUpdatedTask = objectMapper.writeValueAsString(updatedTask);
		mvc.perform(put("/api/todo/" + task.id())
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonUpdatedTask))
				.andExpect(status().isOk())
				.andExpect(content().json(jsonUpdatedTask));
	}

	@Test
	void putTask_returnsBadRequest_withMissingDescription() throws Exception {
		Task task = new Task("T1", "text before", TaskStatus.OPEN);
		repo.save(task);

		Task invalidTask = task.withDescription("");

		mvc.perform(put("/api/todo/" + task.id())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidTask)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void putTask_returnsBadRequest_withMismatchingIds() throws Exception {
		Task task = new Task("T1", "text before", TaskStatus.OPEN);
		repo.save(task);
		repo.save(task.withId("T2"));

		Task invalidTask = task.withId("T2");

		mvc.perform(put("/api/todo/" + task.id())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidTask)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void putTask_returnsBadRequest_withInvalidID() throws Exception {
		Task task = new Task("T1", "text before", TaskStatus.OPEN);
		repo.save(task);

		Task invalidTask = task.withId("T2");

		mvc.perform(put("/api/todo/" + invalidTask.id())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidTask)))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteTask_returnsOk_withValidId() throws Exception {
		Task task = new Task("T1", "to be deleted", TaskStatus.OPEN);
		repo.save(task);

		mvc.perform(delete("/api/todo/" + task.id()))
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteTask_returnsNotFound_withInvalidId() throws Exception {
		mvc.perform(delete("/api/todo/T1"))
				.andExpect(status().isNotFound());
	}
}
