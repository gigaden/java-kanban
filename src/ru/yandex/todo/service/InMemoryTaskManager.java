package ru.yandex.todo.service;

import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    // Класс для управления задачами
    private static int id; // Переменная для хранения id задачи
    private final HashMap<Integer, Task> tasks; // Хранилище всех простых задач
    private final HashMap<Integer, Epic> epics; // Хранилище всех эпиков
    private final HashMap<Integer, Subtask> subtasks; // Хранилище всех подзадач

    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        id = 1;
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    public static int getTaskId() {
        return id;
    }

    public static void setTaskId() {
        id++;
    }


    // Получаем список всех задач
    /* Если я возвращаю через copyOf, то я копирую только массив, объекты которые в нём лежат
    * ссылки же и, меняя их, они меняются в главной мапе, тест тогда один не проходит.
    * Может я не понял чего просто? */
    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> alltasks = new ArrayList<>(tasks.values());
        alltasks.addAll(epics.values());
        alltasks.addAll(subtasks.values());

        return new ArrayList<>(List.copyOf(alltasks));
    }

    // Получаем все простые задачи
    @Override
    public ArrayList<Task> getAllSimpleTasks() {
        return new ArrayList<>(List.copyOf(tasks.values()));
    }

    // Получаем все эпики
    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(List.copyOf(epics.values()));
    }

    // Получаем все подзадачи
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(List.copyOf(subtasks.values()));
    }

    // Проверяем есть ли задача в менеджере
    public boolean hasTask(int id) {
        return tasks.containsKey(id) || epics.containsKey(id) || subtasks.containsKey(id);
    }

    // Удаляем все задачи
    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    // Удаляем все простые задачи
    @Override
    public void deleteAllSimpleTasks() {
        tasks.clear();
    }

    // Удаляем все эпики
    @Override
    public void delAllEpics() {
        for (Epic epic: epics.values()) {
            epic.clearSubtasks();
        }
        epics.clear();
    }

    // Удаляем все подзадачи
    @Override
    public void delAllSubtasks() {
        for (Subtask subtask: subtasks.values()) {
            subtask.getEpic().delSubtaskById(subtask.getTaskId());
        }
        subtasks.clear();
    }

    // Удаляем все подзадачи Эпика
    @Override
    public void delAllSubtasks(Epic epic) {
        for (Subtask subtask: epic.getSubtasks().values()) {
            subtasks.remove(subtask.getTaskId());
        }
        epic.clearSubtasks();
    }

    // Добавляем задачу в менеджер
    @Override
    public int addTask(Task task) {
        tasks.put(task.getTaskId(), task);
        return task.getTaskId();
    }

    // Добавляем задачу в менеджер
    @Override
    public int addEpic(Epic epic) {
        epics.put(epic.getTaskId(), epic);
        return epic.getTaskId();
    }

    // Добавляем задачу в менеджер
    @Override
    public int addSubtask(Epic epic, Subtask subtask) {
        epic.addSubtask(subtask);
        subtasks.put(subtask.getTaskId(), subtask);
        return subtask.getTaskId();
    }

    // Обновляем задачу
    @Override
    public void updateTask(int id, String name, String description) {
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            task.setName(name);
            task.setDescription(description);
        }
    }

    // Обновляем эпик
    @Override
    public void updateEpic(int id, String name, String description) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            epic.setName(name);
            epic.setDescription(description);
        }
    }

    // Обновляем подзадачу
    @Override
    public void updateSubtask(int id, String name, String description) {
        if (subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            subtask.setName(name);
            subtask.setDescription(description);
        }
    }

    // Меняем статус задачи
    @Override
    public void updateTaskStatus(int id, TaskStatus taskStatus) {
        Task task = tasks.get(id);
        if (task.getClass() != Epic.class) {
            task.setTaskStatus(taskStatus);
        }
    }

    // Удаляем задачу по id
    @Override
    public void delTaskById(int id) {
        tasks.remove(id);
    }

    // Удаляем эпик по id
    @Override
    public void delEpicById(int id) {
        delAllSubtasks(epics.get(id));
        epics.remove(id);
    }

    // Удаляем подзадачу по id
    @Override
    public void delSubtaskById(int id) {
        Epic epic = subtasks.get(id).getEpic();
        epic.delSubtaskById(id);
        subtasks.remove(id);
    }

    // Получаем задачу по id
    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    // Получаем эпик по id
    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    // Получаем подзадачу по id
    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // Получаем все подзадачи эпика по id
    @Override
    public ArrayList<Subtask> getAllSubtasksById(int id) {
        return new ArrayList<>(List.copyOf(epics.get(id).getSubtasks().values()));
    }

    // Меняем статус задачи
    @Override
    public void setTaskStatus(int id, TaskStatus taskStatus) {
        if (!hasTask(id) || tasks.get(id).getClass() == Epic.class) {
            return;
        }
        tasks.get(id).setTaskStatus(taskStatus);
    }

    // Получаем историю из 10 последних просмотренных задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void addTaskToHistory(Task task) {
        historyManager.add(task);
    }

}
