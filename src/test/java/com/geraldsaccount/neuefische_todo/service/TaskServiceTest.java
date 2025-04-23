package com.geraldsaccount.neuefische_todo.service;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.geraldsaccount.neuefische_todo.model.Task;
import com.geraldsaccount.neuefische_todo.model.TaskStatus;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

public class TaskServiceTest {
	private TaskService service;

	private TaskRepo repo;
	private IdService idService;

	@BeforeEach
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
}
