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

import com.geraldsaccount.neuefische_todo.model.openai.OpenAiException;
import com.geraldsaccount.neuefische_todo.model.tasks.Task;
import com.geraldsaccount.neuefische_todo.model.tasks.TaskStatus;
import com.geraldsaccount.neuefische_todo.model.tasks.dto.TaskDTO;
import com.geraldsaccount.neuefische_todo.repository.TaskRepo;

public class TaskServiceTest {
	private TaskService service;

	private TaskRepo repo;
	private IdService idService;
	private CorrectionService correctionService;

	@BeforeEach
	@SuppressWarnings("unused")
	void setUp() {
		repo = mock(TaskRepo.class);
		idService = mock(IdService.class);
		correctionService = mock(CorrectionService.class);
		service = new TaskService(repo, idService, correctionService);
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
	void createTask_returnsTask_withValidDto() throws OpenAiException {
		String id = "T1";
		TaskDTO dto = new TaskDTO("Test postin", TaskStatus.OPEN);
		when(idService.generateId()).thenReturn(id);
		when(correctionService.getCorrectedText(any()))
				.thenAnswer(a -> {
					return a.getArgument(0);
				});

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
	void updateTask_updates_withValidData() throws TodoNotFoundException, OpenAiException {
		Task task = new Task("T1", "initial text", TaskStatus.OPEN);
		when(repo.existsById(task.id())).thenReturn(true);
		when(repo.findById(task.id())).thenReturn(Optional.of(task));
		when(correctionService.getCorrectedText(any()))
				.thenAnswer(a -> {
					return a.getArgument(0);
				});

		Task requestedtask = task.withDescription("updated text")
				.withStatus(TaskStatus.DONE);

		assertThat(service.updateTask(task.id(), requestedtask))
				.isEqualTo(requestedtask);

		verify(repo, times(1))
				.save(requestedtask);
	}

	@Test
	void updateTask_throwsIllegalArgument_withInvalidId() throws OpenAiException {
		String invalidId = "T2";
		Task task = new Task("T1", "initial text", TaskStatus.OPEN);
		when(repo.existsById(invalidId)).thenReturn(false);
		when(repo.findById(invalidId)).thenReturn(Optional.empty());

		Task requestedtask = task.withDescription("updated text")
				.withStatus(TaskStatus.DONE);

		assertThatThrownBy(() -> service.updateTask(invalidId, requestedtask))
				.isExactlyInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot update todo. Missing informations.");

		verify(correctionService, never()).getCorrectedText(any());
		verify(repo, never()).save(any());
	}

	@Test
	void updateTask_throwsIllegalArgument_withEmptyDescription() throws OpenAiException {
		Task requestedtask = new Task("T1", "", TaskStatus.OPEN);

		assertThatThrownBy(() -> service.updateTask("T2", requestedtask))
				.isExactlyInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot update todo. Missing informations.");

		assertThatThrownBy(() -> service.updateTask("T2", requestedtask));

		verify(correctionService, never()).getCorrectedText(any());
		verify(repo, never()).existsById(any());
		verify(repo, never()).findById(any());
		verify(repo, never()).save(any());
	}

	@Test
	void updateTask_throwsIllegalArgument_withMisMatchingId() throws OpenAiException {
		Task requestedtask = new Task("T1", "initial text", TaskStatus.OPEN);

		assertThatThrownBy(() -> service.updateTask("T2", requestedtask))
				.isExactlyInstanceOf(IllegalArgumentException.class)
				.hasMessage("Cannot update todo. Missing informations.");

		verify(correctionService, never()).getCorrectedText(any());
		verify(repo, never()).findById(any());
		verify(repo, never()).existsById(any());
		verify(repo, never()).save(any());
	}

	@Test
	void deleteTask_deletes_withValidId() throws TodoNotFoundException {
		String id = "T1";
		Task mockTask = mock(Task.class);
		when(repo.findById(id)).thenReturn(Optional.of(mockTask));

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
