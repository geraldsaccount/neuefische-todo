package com.geraldsaccount.neuefische_todo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

	@Test
	void getById_returnsEmpty_withInvalidId() {
		String invalidId = "T1";
		when(repo.findById(invalidId)).thenReturn(Optional.empty());

		assertThat(service.getById(invalidId)).isEmpty();
	}

	@Test
	void getById_returnsTask_withValidId() {
		Task task = new Task("T1", "Test getting tasks by id", TaskStatus.OPEN);
		when(repo.findById(task.id())).thenReturn(Optional.of(task));

		assertThat(service.getById(task.id())).contains(task);
	}

	@Test
	void updateTask_updates_withValidData() {
		Task task = new Task("T1", "initial text", TaskStatus.OPEN);
		when(repo.findById(task.id())).thenReturn(Optional.of(task));

		Task requestedtask = task.withDescription("updated text")
				.withStatus(TaskStatus.DONE);

		assertThat(service.updateTask(task.id(), requestedtask))
				.isNotEmpty()
				.hasValue(requestedtask);

		verify(repo, times(1))
				.save(requestedtask);
	}

	@Test
	void updateTask_stopsProcess_withInvalidId() {
		Task task = new Task("T1", "initial text", TaskStatus.OPEN);
		when(repo.findById("T2")).thenReturn(Optional.empty());

		Task requestedtask = task.withDescription("updated text")
				.withStatus(TaskStatus.DONE);

		assertThat(service.updateTask("T2", requestedtask))
				.isEmpty();

		verify(repo, never()).save(any());
	}

	@Test
	void updateTask_stopsProcess_withEmptyDescription() {
		Task requestedtask = new Task("T1", "", TaskStatus.OPEN);

		assertThat(service.updateTask("T2", requestedtask))
				.isEmpty();

		verify(repo, never()).findById(any());
		verify(repo, never()).save(any());
	}

	@Test
	void updateTask_stopsProcess_withMisMatchingId() {
		Task requestedtask = new Task("T1", "initial text", TaskStatus.OPEN);

		assertThat(service.updateTask("T2", requestedtask))
				.isEmpty();

		verify(repo, never()).findById(any());
		verify(repo, never()).save(any());
	}

	@Test
	void deleteTask_deletes_withValidId() {
		String id = "T1";
		Task mockTask = mock(Task.class);
		when(repo.findById(id)).thenReturn(Optional.of(mockTask));

		assertThat(service.delete(id)).isTrue();

		verify(repo, times(1)).deleteById(id);
	}

	@Test
	void deleteTask_stopsProcess_withInvalidId() {
		String invalidId = "T1";
		when(repo.existsById(invalidId)).thenReturn(false);

		assertThat(service.delete(invalidId)).isFalse();

		verify(repo, never()).deleteById(any());
	}
}
