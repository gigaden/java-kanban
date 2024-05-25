package ru.yandex.todo.filltasks;

import ru.yandex.todo.model.Task;
import ru.yandex.todo.service.TaskManager;

import java.time.LocalDateTime;

public class CreateTasks {
    public static void createTasks(TaskManager taskManager) {
        LocalDateTime localDateTime = LocalDateTime.now();

        // Создаём задачи, эпики и подзадачи
        Task task1 = new Task("Задача 1", "Описание задачи 1", localDateTime.plusDays(1), 60);
        taskManager.addTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2", localDateTime.plusDays(2), 80);
        taskManager.addTask(task2);
        Task task3 = new Task("Задача 3", "Описание задачи 3", localDateTime.plusDays(3), 90);
        taskManager.addTask(task3);
        System.out.println();

    }
}