package ru.yandex.todo.model;

import ru.yandex.todo.service.InMemoryTaskManager;
import ru.yandex.todo.service.TaskStatus;

import java.util.HashMap;

public class Task {
    //  Класс для описания главных задач
    protected int taskId;
    protected String name;
    protected String description;
    protected TaskStatus taskStatus;

    protected HashMap<Integer, Subtask> subtasks;

    public Task() {

    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        taskStatus = TaskStatus.NEW;
        taskId = InMemoryTaskManager.getTaskId();
        InMemoryTaskManager.setTaskId();
        subtasks = null;
    }

    // Конструктор для глубокого копирования объекта
    public Task(Task another) {
        this.taskId = another.taskId;
        this.name = another.name;
        this.description = another.description;
        this.taskStatus = another.taskStatus;
        this.subtasks = another.subtasks;
    }

    // Получаем все подзадачи. Для обычной задачи и подзадачи вернётся null, для эпика вернётся хэшмап
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    // Получаем имя задачи
    public String getName() {
        return name;
    }

    // Меняем имя задачи
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getTaskId() {
        return taskId;
    }


    @Override
    public String toString() {
        return String.format("Task id= %d | имя: %s\n" +
                "описание: %s\n" +
                "статус: %s\n", getTaskId(), getName(), getDescription(), getTaskStatus());
    }
}
