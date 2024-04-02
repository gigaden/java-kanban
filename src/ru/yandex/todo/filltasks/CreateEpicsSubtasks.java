package ru.yandex.todo.filltasks;

import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.service.TaskManager;

public class CreateEpicsSubtasks {
    public static void createEpics(TaskManager taskManager) {

        // Создаём задачи, эпики и подзадачи
        Epic epic1 = new Epic("Эпик 1", "Описание Эпика 1");
        taskManager.addTask(epic1);
        Epic epic2 = new Epic("Эпик 2", "Описание Эпика 2");
        taskManager.addTask(epic2);
        Epic epic3 = new Epic("Эпик 3", "Описание Эпика 3");
        taskManager.addTask(epic3);
        System.out.println();

        // добавляем подзадачи в эпики
        Subtask subtask1 = new Subtask(epic1, "Субтаск 1", "Описание субтаска 1");
        epic1.addSubtask(subtask1);
        taskManager.addTask(subtask1);
        Subtask subtask2 = new Subtask(epic1,"Субтаск 2", "Описание субтаска 2");
        epic1.addSubtask(subtask2);
        taskManager.addTask(subtask2);
        Subtask subtask3 = new Subtask(epic2,"Субтаск 3", "Описание субтаска 3");
        epic2.addSubtask(subtask3);
        taskManager.addTask(subtask3);
        Subtask subtask4 = new Subtask(epic1,"Субтаск 4", "Описание субтаска 4");
        epic1.addSubtask(subtask4);
        taskManager.addTask(subtask4);
    }
}