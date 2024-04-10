package ru.yandex.todo.service;

import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {


    // Получаем список всех задач
    ArrayList<Task> getAllTasks();

    // Получаем список всех простых задач
    ArrayList<Task> getAllSimpleTasks();

    // Получаем все эпики
    ArrayList<Epic> getAllEpics();

    // Получаем все подзадачи
    ArrayList<Subtask> getAllSubtasks();

    // Добавляем задачу в менеджер
    int addTask(Task task);

    // Добавляем эпик в менеджер
    int addEpic(Epic epic);

    // Добавляем подзадачу в менеджер
    int addSubtask(Epic epic, Subtask task);

    // Удаляем все задачи
    void deleteAllTasks();

    // Удаляем все простые задачи
    void deleteAllSimpleTasks();

    // Удаляем все эпики
    void delAllEpics();

    // Удаляем все подзадачи
    void delAllSubtasks();

    // Удаляем все подзадачи эпика
    void delAllSubtasks(Epic epic);

    // Обновляем задачу
    void updateTask(int id, String name, String description);

    // Обновляем эпик
    void updateEpic(int id, String name, String description);

    // Обновляем подзадачу
    void updateSubtask(int id, String name, String description);

    // Меняем статус задачи
    void updateTaskStatus(int id, TaskStatus taskStatus);

    // Удаляем задачу по id
    void delTaskById(int id);

    // Удаляем эпик по id
    void delEpicById(int id);

    // Удаляем  подзадачу по id
    void delSubtaskById(int id);

    // Получаем задачу по id
    Task getTaskById(int id);

    // Получаем эпик по id
    Epic getEpicById(int id);

    // Получаем подзадачу по id
    Subtask getSubtaskById(int id);

    // Получаем все подзадачи эпика по id
    ArrayList<Subtask> getAllSubtasksById(int id);

    // Меняем статус задачи
    void setTaskStatus(int id, TaskStatus taskStatus);

    // Получаем историю из 10 последних просмотренных задач
    List<Task> getHistory();

    // Добавляем просмотренные задачи в историю
    void addTaskToHistory(Task task);

    // Проверяем есть ли таска в менеджере
    boolean hasTask(int id);
}
