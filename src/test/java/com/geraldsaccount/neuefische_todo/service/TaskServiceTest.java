package com.geraldsaccount.neuefische_todo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
				.isEqualTo(expected);

		verify(repo).save(expected);
	}

	@Test
	void createTask_throwsInvalidArgument_withInvalidDto() {
		TaskDTO dto = new TaskDTO("", TaskStatus.OPEN);

		assertThatThrownBy(() -> service.createTask(dto))
				.isExactlyInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot create todo. Missing informations.");

		verify(idService, never()).generateId();
		verify(repo, never()).save(any());
	}

	@Test
	void getById_throwsTodoNotFound_withInvalidId() {
		String invalidId = "T1";
		when(repo.findById(invalidId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> service.getById(invalidId))
				.isExactlyInstanceOf(TodoNotFoundException.class)
				.hasMessage("Todo with id " + invalidId + "was not found.");
	}

	@Test
	void getById_returnsTask_withValidId() throws TodoNotFoundException {
		Task task = new Task("T1", "Test getting tasks by id", TaskStatus.OPEN);
		when(repo.findById(task.id())).thenReturn(Optional.of(task));

		assertThat(service.getById(task.id()))
				.isEqualTo(task);
	}

	@Test
	void updateTask_updates_withValidData() throws TodoNotFoundException {
		Task task = new Task("T1", "initial text", TaskStatus.OPEN);
		when(repo.existsById(task.id())).thenReturn(true);

		Task requestedtask = task.withDescription("updated text")
				.withStatus(TaskStatus.DONE);

		assertThat(service.updateTask(task.id(), requestedtask))
				.isEqualTo(requestedtask);

		verify(repo, times(1))
				.save(requestedtask);
	}

	@Test
	void updateTask_throwsIllegalArgument_withInvalidId() {
		String invalidId = "T2";
		Task task = new Task("T1", "initial text", TaskStatus.OPEN);
		when(repo.existsById(invalidId)).thenReturn(false);

		Task requestedtask = task.withDescription("updated text")
				.withStatus(TaskStatus.DONE);

		assertThatThrownBy(() -> service.updateTask(invalidId, requestedtask))
				.isExactlyInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot update todo. Missing informations.");

		verify(repo, never()).save(any());
	}

	@Test
	void updateTask_throwsIllegalArgument_withEmptyDescription() {
		Task requestedtask = new Task("T1", "", TaskStatus.OPEN);

		assertThatThrownBy(() -> service.updateTask("T2", requestedtask))
				.isExactlyInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot update todo. Missing informations.");

		assertThatThrownBy(() -> service.updateTask("T2", requestedtask));

		verify(repo, never()).existsById(any());
		verify(repo, never()).save(any());
	}

	@Test
	void updateTask_throwsIllegalArgument_withMisMatchingId() {
		Task requestedtask = new Task("T1", "initial text", TaskStatus.OPEN);

		assertThatThrownBy(() -> service.updateTask("T2", requestedtask))
				.isExactlyInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot update todo. Missing informations.");

		verify(repo, never()).existsById(any());
		verify(repo, never()).save(any());
	}

	@Test
	void deleteTask_deletes_withValidId() throws TodoNotFoundException {
		String id = "T1";
		when(repo.existsById(id)).thenReturn(true);

		service.delete(id);

		verify(repo, times(1)).deleteById(id);
	}

	@Test
	void deleteTask_throwsTodoNotFound_withInvalidId() {
		String invalidId = "T1";
		when(repo.existsById(invalidId)).thenReturn(false);
		assertThatThrownBy(() -> service.delete(invalidId))
				.isInstanceOf(TodoNotFoundException.class)
				.hasMessage("Todo with id " + invalidId + "was not found.");
		verify(repo, never()).deleteById(any());
	}
}
