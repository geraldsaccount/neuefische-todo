package com.geraldsaccount.neuefische_todo.controller;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geraldsaccount.neuefische_todo.model.tasks.Task;
import com.geraldsaccount.neuefische_todo.model.tasks.TaskStatus;
import com.geraldsaccount.neuefische_todo.model.tasks.dto.TaskDTO;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;
import com.geraldsaccount.neuefische_todo.service.CorrectionService;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // needed to reset mvc before each test
public class TaskControllerTest {
	private final String uri = "/api/todo";

	@Autowired
	private TaskRepo repo;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private CorrectionService correctionService;

	// region getTasks
	@Test
	void getTasks_returnsTasks_whenCalled() throws Exception {
		Task task = new Task("T1", "this should return", TaskStatus.DONE);
		repo.save(task);

		mvc.perform(get(uri))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(List.of(task))));
	}
	// endregion

	// region post
	@Test
	void postTask_returnsTask_withValidDto() throws Exception {
		TaskDTO dto = new TaskDTO("this should become a task", TaskStatus.IN_PROGRESS);
		String jsonDto = objectMapper.writeValueAsString(dto);

		when(correctionService.getCorrectedText(any()))
				.thenAnswer(a -> {
					return a.getArgument(0);
				});

		mvc.perform(post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonDto))
				.andExpect(status().isCreated())
				.andExpect(content().json(jsonDto))
				.andExpect(jsonPath("$.id").isNotEmpty());
	}

	@Test
	void postTask_returnsBadRequest_withInvalidDto() throws Exception {
		TaskDTO dto = new TaskDTO(null, TaskStatus.IN_PROGRESS);

		mvc.perform(post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
				.andExpect(status().isBadRequest());
	}

	// endregion

	// region getById
	@Test
	void getById_returnsNotFound_withInvalidId() throws Exception {
		Task task = new Task("T1", "should not be found", TaskStatus.OPEN);
		repo.save(task);

		mvc.perform(get(uri + "/T2"))
				.andExpect(status().isNotFound());
	}

	@Test
	void getById_returnsTask_withValidId() throws Exception {
		Task task = new Task("T1", "should be found", TaskStatus.OPEN);
		repo.save(task);

		mvc.perform(get(uri + "/" + task.id()))
				.andExpect(status().isOk())
				.andExpect(content().json(objectMapper.writeValueAsString(task)));
	}

	// endregion

	// region updateTask
	@Test
	void putTask_updatesTask_withValidData() throws Exception {
		when(correctionService.getCorrectedText(any()))
				.thenAnswer(a -> {
					return a.getArgument(0);
				});

		Task task = new Task("T1", "text before", TaskStatus.OPEN);
		repo.save(task);

		Task updatedTask = task
				.withDescription("updated text")
				.withStatus(TaskStatus.DONE);

		String jsonUpdatedTask = objectMapper.writeValueAsString(updatedTask);
		mvc.perform(put(uri + "/" + task.id())
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

		mvc.perform(put(uri + "/" + task.id())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidTask)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void putTask_returnsBadRequest_withMismatchingIds() throws Exception {
		String mismatchingId = "T2";
		Task task = new Task("T1", "text before", TaskStatus.OPEN);
		repo.save(task);
		repo.save(task.withId(mismatchingId));

		Task invalidTask = task.withId(mismatchingId);

		mvc.perform(put(uri + "/" + task.id())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidTask)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void putTask_returnsBadRequest_withInvalidID() throws Exception {
		Task task = new Task("T1", "text before", TaskStatus.OPEN);
		repo.save(task);

		Task invalidTask = task.withId("T2");

		mvc.perform(put(uri + "/" + invalidTask.id())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidTask)))
				.andExpect(status().isNotFound());
	}
	// endregion

	// region delete
	@Test
	void deleteTask_returnsOk_withValidId() throws Exception {
		Task task = new Task("T1", "to be deleted", TaskStatus.OPEN);
		repo.save(task);

		mvc.perform(delete(uri + "/" + task.id()))
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteTask_returnsNotFound_withInvalidId() throws Exception {
		mvc.perform(delete(uri + "/T1"))
				.andExpect(status().isNotFound());
	}
	// endregion

	// region undo
	@Test
	void undo_resetsState_withActionsToReset() throws JsonProcessingException, Exception {
		when(correctionService.getCorrectedText(any()))
				.thenAnswer(a -> {
					return a.getArgument(0);
				});

		TaskDTO task = new TaskDTO("this task should be deleted", TaskStatus.DONE);
		mvc.perform(post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(task)))
				.andExpect(status().isCreated());

		mvc.perform(put(uri + "/undo"))
				.andExpect(status().isOk());

		List<Task> state = repo.findAll();

		assertThat(state).isEmpty();
	}

	@Test
	void undo_returnsBadRequest_withEmptyHistory() throws JsonProcessingException, Exception {
		when(correctionService.getCorrectedText(any()))
				.thenAnswer(a -> {
					return a.getArgument(0);
				});

		mvc.perform(put(uri + "/undo"))
				.andExpect(status().isBadRequest());
	}
	// endregion

	// region redo
	@Test
	void redo_resetsState_withActionToReset() throws JsonProcessingException, Exception {
		when(correctionService.getCorrectedText(any()))
				.thenAnswer(a -> {
					return a.getArgument(0);
				});

		TaskDTO task = new TaskDTO("this task should be deleted", TaskStatus.DONE);

		mvc.perform(post(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(task)))
				.andExpect(status().isCreated());
		List<Task> oldState = repo.findAll();

		mvc.perform(put(uri + "/undo"))
				.andExpect(status().isOk());
		mvc.perform(put(uri + "/redo"))
				.andExpect(status().isOk());

		List<Task> state = repo.findAll();

		assertThat(state)
				.containsExactlyInAnyOrderElementsOf(oldState);
	}

	@Test
	void redo_returnsBadRequest_withEndOfHistory() throws JsonProcessingException, Exception {
		when(correctionService.getCorrectedText(any()))
				.thenAnswer(a -> {
					return a.getArgument(0);
				});

		mvc.perform(put(uri + "/redo"))
				.andExpect(status().isBadRequest());
	}
	// endregion
}
