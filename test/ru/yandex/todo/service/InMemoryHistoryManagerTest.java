package ru.yandex.todo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.todo.model.Task;

public class InMemoryHistoryManagerTest {

    InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        // Наполняем задачами
        for (int i = 0; i < InMemoryHistoryManager.historySize - 1; i++) {
            taskManager.addTask(new Task("Task " + 1, "Description " + 1));
            taskManager.getTaskById(i + 1); // Добавляем историю просмотров
        }
    }

    @Test // Проверяем добавляются ли задачи в историю
    public void shouldBePositiveWhenTasksAddedToHistory() {
        Assertions.assertNotNull(taskManager.getHistory(), "Истории нет.");
        taskManager.getTaskById(1);
        Assertions.
                assertEquals(InMemoryHistoryManager.historySize,
                        taskManager.getHistory().size(), "Добавлены не все таски в историю");
    }

    @Test // Проверяем неизменность размера массива истории при добавлении новых задач
    public void shouldBePositiveWhenAddedMoreTasksThanSizeOfHistoryArray() {
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        Assertions.assertEquals(10, taskManager.getHistory().size(), "Размер массива истории увеличен");
    }

    @Test // Проверяем, что в случае выхода за размер массива историй новая таска добавится на место первого элемента
    public void shouldBePositiveWhenArrayIsFullAndNewElementBecomeFirst() {
        taskManager.getTaskById(1);
        taskManager.getTaskById(5);
        Task newFirstTaskInHistory = taskManager.getHistory().getFirst();
        Assertions.assertEquals(5, newFirstTaskInHistory.getTaskId(), "Задачи не совпадают");
    }

    @Test // Проверяем, что копия задачи сохраняется в истории после её удаления
    public void shouldBePositiveWhenTaskSavesInHistoryAfterDeleting() {
        Task copy = new Task(taskManager.getTaskById(1));
        taskManager.delTaskById(1);
        Task taskInHistory = taskManager.getHistory().getFirst();
        Assertions.assertEquals(copy, taskInHistory, "Задача не сохранилась в истории после удаления");

    }
}
