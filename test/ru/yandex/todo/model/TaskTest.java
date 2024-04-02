package ru.yandex.todo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.yandex.todo.service.Managers;
import ru.yandex.todo.service.TaskManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class TaskTest {

    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void shouldBePositiveWhenTaskIsAddedAndIdIsEquals() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void shouldBePositiveWhenChildsIdOfTaskIsEquals() {
        Task epic = new Epic("Test addNewTask", "Test addNewTask description");
        final int epicId = taskManager.addTask(epic);

        final Task savedTask = taskManager.getTaskById(epicId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test
    public void shouldBePositiveWhenSubtaskAddedToEpic() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        Task subtask = new Subtask(epic, "newSubtask", "description");
        taskManager.addTask(epic);
        taskManager.addTask(subtask);
        assertNotNull(taskManager.getAllSubtasksById(epic.getTaskId()), "Подзадач нет.");
        assertNotNull(epic.getSubtaskById(subtask.getTaskId()), "Подзадачи нет в мапе эпика");
    }





}