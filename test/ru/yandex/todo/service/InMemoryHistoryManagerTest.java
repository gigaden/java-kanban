package ru.yandex.todo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.util.List;

public class InMemoryHistoryManagerTest {

    InMemoryTaskManager taskManager;
    static final int historySize = 10; // Размер истории для тестов

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        // Наполняем задачами
        for (int i = 0; i < historySize; i++) {
            taskManager.addTask(new Task("Task " + 1, "Description " + 1));
            taskManager.getTaskById(i + 1); // Добавляем историю просмотров
        }
    }

    @Test // Проверяем добавляются ли задачи в историю
    public void shouldBePositiveWhenTasksAddedToHistory() {
        Assertions.assertFalse(taskManager.getHistory().isEmpty(), "Истории нет.");
        Assertions.
                assertEquals(historySize,
                        taskManager.getHistory().size(), "Добавлены не все таски в историю");
    }

    @Test // Проверяем, что в случае повторного просмотра предыдущий просмотр будет удалён, а новый добавлен в конец
    public void shouldBePositiveWhenAddedViewedTaskAndSameTaskBecomeLast() {
        Task pastTask = taskManager.getHistory().get(1);
        taskManager.getTaskById(pastTask.getTaskId());
        Assertions.assertNotSame(pastTask, taskManager.getHistory().get(1), "Задачи одинаковые");
    }

    @Test // Проверяем, что копия задачи не сохраняется в истории после её удаления
    public void shouldBePositiveWhenTaskNotSavesInHistoryAfterDeleting() {
        List<Task> historyList = taskManager.getHistory();
        taskManager.delTaskById(1);
        List<Task> newHistoryList = taskManager.getHistory();
        Assertions.assertNotEquals(historyList.size(), newHistoryList.size(), "Задача не удалилась из истории");

    }

    @Test // Проверяем, что несуществующая задача не будет добавлена в историю
    public void shouldBePositiveWhenAddedUnexistingTaskInHistory() {
        taskManager.getTaskById(-1);
        taskManager.getTaskById(16);
        taskManager.getSubtaskById(55, 60);
        Assertions.assertEquals(historySize, taskManager.getHistory().size());
    }

    @Test // Проверяем, что подзадачи удаляются из истории при удалении эпика
    public void shouldBePositiveWhenDeletingEpicFromTasks() {
        Epic epic = new Epic("epic", "description of epic");
        taskManager.addTask(epic);
        Subtask subtask = new Subtask(epic, "subtask", "description of subtask");
        taskManager.addTask(subtask);
        taskManager.getTaskById(subtask.getTaskId());
        int sizeBefore = taskManager.getHistory().size();
        taskManager.delTaskById(epic.getTaskId());
        int sizeAfter = taskManager.getHistory().size();
        Assertions.assertNotEquals(sizeBefore, sizeAfter, "Подзадача не удалилась из истории");
    }

    @Test // Проверяем, что история очищается при удалении всех задач
    public void shouldBePositiveWhenDeletedAllTaskAndHistoryBecomeEmpty() {
        taskManager.deleteAllTasks();
        Assertions.assertEquals(0, taskManager.getHistory().size(), "История не очищена");
    }

    @Test // Проверяем, что подзадача удалится из истории
    public void shouldBePositiveWhenSubtaskDeleted() {
        Epic epic = new Epic("epic", "description of epic");
        taskManager.addTask(epic);
        Subtask subtask = new Subtask(epic, "subtask", "description of subtask");
        taskManager.addTask(subtask);
        taskManager.getTaskById(subtask.getTaskId());
        Assertions.assertEquals(taskManager.getHistory().size(), historySize + 1, "Субтаск не в истории");
        taskManager.delTaskById(subtask.getTaskId());
        Assertions.assertEquals(taskManager.getHistory().size(), historySize, "Субтаск не удалён из истории");

    }

    @Test // Проверяем, что задачи попадают в конец истории и выводятся с конца
    public void shouldBePositiveWhenAddedTaskInTheEndOfHistoryAndReturnedReversedArray() {
        Task task = new Task("task", "description");
        Task lastTaskInHistory = taskManager.getHistory().getFirst();
        Assertions.assertEquals(historySize, lastTaskInHistory.getTaskId());
        taskManager.addTask(task);
        taskManager.getTaskById(task.getTaskId());
        Assertions.assertEquals(taskManager.getHistory().getFirst(), task,
                "Задача не добавилась в конец истории");
    }
}
