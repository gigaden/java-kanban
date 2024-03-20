package test;

import model.Epic;
import model.Subtask;
import model.Task;
import service.CreateEpicsSubtasks;
import service.CreateTasks;
import service.TaskManager;
import service.TaskStatus;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        System.out.println("Тест запущен");
        TaskManager taskManager = new TaskManager();
        // CreateTasks.createTasks(taskManager); // наполнить тасками
        // CreateEpicsSubtasks.createEpics(taskManager); // наполнить эпиками и подзадачами
        // System.out.println("Наполнили базу");

        // Добавляем задачу и эпик
        Task task1 = new Task("Test 1", "Test 1 description");
        taskManager.addTask(task1);
        System.out.println("Добавили новую задачу:");
        System.out.println(taskManager.getTaskById(task1.getTaskId()));
        System.out.println("Добавили новый эпик:");
        Epic epic1 = new Epic("Test epic 1", "Test epic 1 description");
        taskManager.addTask(epic1);
        System.out.println(taskManager.getTaskById(epic1.getTaskId()));
        System.out.println("______________________________________");

        // Добавляем подзадачу
        Subtask subtask1 = new Subtask( epic1,"Test subtask 1", "Test subtask 1 description");
        taskManager.addTask(subtask1);
        System.out.println("Добавили новую подзадачу в него:");
        System.out.println(taskManager.getTaskById(subtask1.getTaskId()));
        System.out.println("______________________________________");


        System.out.println("Выводим все задачи");
        System.out.println(taskManager.getAllTasks());
        System.out.println("______________________________________");

        System.out.println("Выводим все подзадачи по id эпика");
        System.out.println(taskManager.getAllSubtasksById(epic1.getTaskId()));
        System.out.println("______________________________________");

        System.out.println("Выводим эпик, его статус, меняем статус подзадачи, выводим ещё раз эпик");
        System.out.println(taskManager.getTaskById(epic1.getTaskId()));
        System.out.println("Пробуем поменять статус эпика, меняться не должен");
        taskManager.setTaskStatus(epic1.getTaskId(), TaskStatus.DONE);
        System.out.println(taskManager.getTaskById(epic1.getTaskId()));
        System.out.println("______________________________________");

        System.out.println("Меняем статус подзадачи на IN_PROGRESS");
        taskManager.setTaskStatus(subtask1.getTaskId(), TaskStatus.IN_PROGRESS);
        System.out.println(taskManager.getTaskById(epic1.getTaskId()));
        System.out.println("______________________________________");

        System.out.println("Меняем статус подзадачи на DONE");
        taskManager.setTaskStatus(subtask1.getTaskId(), TaskStatus.DONE);
        System.out.println(taskManager.getTaskById(epic1.getTaskId()));
        System.out.println("______________________________________");

        System.out.println("Удаляем подзадачу");
        taskManager.delTaskById(subtask1.getTaskId());
        System.out.println("Выводим все задачи");
        System.out.println(taskManager.getAllTasks());
        System.out.println("____________________________________");
        System.out.println();

        System.out.println("Получаем список всех эпиков");
        System.out.println("счётчик в таскменеджер:" + TaskManager.getTaskId());
        ArrayList<Epic> allEpics = taskManager.getAllEpics();
        System.out.println(allEpics);
        System.out.println("Пробуем поменять имя одного из эпиков в полученных данных");
        allEpics.get(0).setName("Успешно изменили имя эпика");
        System.out.println(taskManager.getAllEpics());
        System.out.println("счётчик в таскменеджер:" + TaskManager.getTaskId());
        System.out.println("____________________________________");
        System.out.println();

        Subtask subtask = new Subtask(epic1, "Имя подзадачи прежнее", "Описание");
        taskManager.addTask(subtask);
        System.out.println("Получаем список всех подзадач");
        ArrayList<Subtask> allSubtask = taskManager.getAllSubtasks();
        System.out.println(allSubtask);
        System.out.println("Пробуем поменять имя одной из подзадач в полученных данных");
        allSubtask.get(0).setName("Успешно изменили имя подзадачи");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("счётчик в таскменеджер:" + TaskManager.getTaskId());


    }
}
