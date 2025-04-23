package com.geraldsaccount.neuefische_todo.service;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.geraldsaccount.neuefische_todo.model.Task;
import com.geraldsaccount.neuefische_todo.model.TaskStatus;
import com.geraldsaccount.neuefische_todo.model.dto.TaskDTO;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

public class TaskServiceTest {
	private TaskService service;

	private TaskRepo repo;
	private IdService idService;

	@BeforeEach
	@SuppressWarnings("unused")
	void setUp() {
		repo = mock(TaskRepo.class);
		idService = mock(IdService.class);
		service = new TaskService(repo, idService);
	}

	@Test
	void getTasks_returnsTasks_whenCalled() {
		List<Task> tasks = new ArrayList<>(List.of(
				new Task("T1", "Task 1", TaskStatus.OPEN),
				new Task("T2", "Task 2", TaskStatus.OPEN)));
		when(repo.findAll()).thenReturn(tasks);

		assertThat(service.getTasks())
				.containsExactlyElementsOf(tasks);
	}

	@Test
	void createTask_returnsTask_withValidDto() {
		String id = "T1";
		TaskDTO dto = new TaskDTO("Test postin", TaskStatus.OPEN);
		when(idService.generateId()).thenReturn(id);

		Task expected = Task.of(dto).withId(id);
		assertThat(service.createTask(dto))
				.isNotEmpty()
				.hasValue(expected);

		verify(repo).save(expected);
	}

	@Test
	void createTask_returnsEmpty_withInvalidDto() {
		TaskDTO dto = new TaskDTO("", TaskStatus.OPEN);

		assertThat(service.createTask(dto))
				.isEmpty();

		verify(idService, never()).generateId();
		verify(repo, never()).save(any());
	}
}
