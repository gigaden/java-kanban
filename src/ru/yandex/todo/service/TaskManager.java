package ru.yandex.todo.service;

import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.util.List;

public interface TaskManager {


    // Получаем список всех задач
    List<Task> getAllTasks();

    // Получаем все эпики
    List<Epic> getAllEpics();

    // Получаем все подзадачи
    List<Subtask> getAllSubtasks();

    // Удаляем все задачи
    void deleteAllTasks();

    // Удаляем все эпики
    void delAllEpics();

    // Добавляем задачу в менеджер
    void addTask(Task task);

    // Обновляем задачу
    void updateTask(int id, String name, String description);

    // Меняем статус задачи
    void updateTaskStatus(int id, TaskStatus taskStatus);

    // Удаляем задачу по id
    void delTaskById(int id);

    // Удаляем все подзадачи эпика
    void delAllSubtasks(Task task);

    // Получаем задачу по id
    Task getTaskById(int id);

    // Получаем подзадачу по id эпика и id подзадачи
    Subtask getSubtaskById(int taskId, int subtaskId);

    // Получаем все подзадачи эпика по id
    List<Subtask> getAllSubtasksById(int id);

    // Меняем статус задачи
    void setTaskStatus(int id, TaskStatus taskStatus);

    // Получаем историю из 10 последних просмотренных задач
    List<Task> getHistory();

    // Добавляем просмотренные задачи в историю
    void addTaskToHistory(Task task);

    // Проверяем есть ли таска в менеджере
    boolean hasTask(int id);

    // Получаем задача по приоритету
    public List<Task> getPrioritizedTasks();
}
