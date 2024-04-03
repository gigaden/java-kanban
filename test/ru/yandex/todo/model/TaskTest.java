package ru.yandex.todo.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import ru.yandex.todo.service.Managers;
import ru.yandex.todo.service.TaskManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class TaskTest {

    private TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test // Проверяем добавляется ли задачи
    public void shouldBePositiveWhenTaskIsAdded() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.addTask(task);
        Assertions.assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test // Проверяем получение задачи по id
    public void shouldBePositiveWhenTaskIsAddedAndIdIsEquals() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test // Проверяем получение эпика по id
    public void shouldBePositiveWhenChildsIdOfTaskIsEquals() {
        Task epic = new Epic("Test addNewTask", "Test addNewTask description");
        final int epicId = taskManager.addTask(epic);

        final Task savedTask = taskManager.getTaskById(epicId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");
    }

    @Test // Проверяем добавление подзадачи в эпик
    public void shouldBePositiveWhenSubtaskAddedToEpic() {
        Epic epic = new Epic("Test addNewTask", "Test addNewTask description");
        Task subtask = new Subtask(epic, "newSubtask", "description");
        taskManager.addTask(epic);
        taskManager.addTask(subtask);
        assertNotNull(taskManager.getAllSubtasksById(epic.getTaskId()), "Подзадач нет.");
        assertNotNull(epic.getSubtaskById(subtask.getTaskId()), "Подзадачи нет в мапе эпика");
    }

    @Test // Проверяем возможность поменять задачу в главной мапе через метод получения всех задач
    public void shouldBePositiveIfWeCantChangeTask() {
        Task task = new Task("newSubtask", "description");
        taskManager.addTask(task);
        Task newTask = taskManager.getAllTasks().getFirst();
        newTask.setName("new name of task");
        Assertions.assertNotEquals(task.getName(), newTask.getName(), "Задача изменена");
    }




}