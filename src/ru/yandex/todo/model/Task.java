package ru.yandex.todo.model;

import ru.yandex.todo.service.InMemoryTaskManager;
import ru.yandex.todo.service.TaskStatus;
import ru.yandex.todo.service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

public class Task {
    //  Класс для описания главных задач
    protected int taskId;
    protected String name;
    protected String description;
    protected TaskStatus taskStatus;

    protected LocalDateTime startTime; // Дата и время, когда предполагается приступить к выполнению задачи
    protected Duration duration; // Продолжительность задачи
    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    protected HashMap<Integer, Subtask> subtasks;

    public Task() {

    }

    public Task(String name, String description, LocalDateTime startTime, int duration) {
        this.name = name;
        this.description = description;
        taskStatus = TaskStatus.NEW;
        taskId = InMemoryTaskManager.getTaskId();
        InMemoryTaskManager.setTaskId();
        subtasks = null;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);


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

    // Меняем id
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int duration) {
        this.duration = Duration.ofMinutes(duration);
    }

    // Рассчитываем дату и время завершения задачи
    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }


    @Override
    public String toString() {

        return String.format("%d, %s, %s, %s, %s, %s, %s, %s",
                getTaskId(), TaskType.TASK.toString(), getName(), getTaskStatus(),
                getDescription(), startTime != null ? startTime.format(dateTimeFormatter) : null, duration.toMinutes(),
                getEndTime() != null ? getEndTime().format(dateTimeFormatter) : null);
    }

    // Переопределяем для сравнения объектов по id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && startTime == task.startTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
