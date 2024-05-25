package ru.yandex.todo.model;

import ru.yandex.todo.service.InMemoryTaskManager;
import ru.yandex.todo.service.TaskStatus;
import ru.yandex.todo.service.TaskType;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {
    // Класс для создания эпика

    protected LocalDateTime endTime;

    public Epic() {
        subtasks = new HashMap<>();
    }


    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        taskStatus = TaskStatus.NEW;
        subtasks = new HashMap<>();
        taskId = InMemoryTaskManager.getTaskId();
        InMemoryTaskManager.setTaskId();
    }

    // Конструктор для глубокого копирования
    public Epic(Epic another) {
        this.taskId = another.taskId;
        this.name = another.name;
        this.description = another.description;
        this.taskStatus = another.taskStatus;
        this.subtasks = another.getCopyOfSubtasks();
        this.startTime = another.startTime;
        this.endTime = another.endTime;
    }


    // Добавляем подзадачу в эпик
    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getTaskId(), subtask);
    }

    // Очищаем подзадачи в эпике
    public void clearSubtasks() {
        subtasks.clear();
    }

    // Находим подзадачу в эпике по id
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    // Удаляем субтаск по id
    public void delSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            subtasks.remove(id);
        }
    }

    // Проверяем статусы субтасков в эпике
    public void checkSubtasksStatusDone() {
        boolean statusDone = true;
        boolean allIsNew = true;
        for (Subtask subtask : subtasks.values()) {
            if (subtask.taskStatus != TaskStatus.DONE) {
                statusDone = false;
            }
            if (subtask.taskStatus != TaskStatus.NEW) {
                allIsNew = false;
            }
        }
        if (statusDone) {
            setTaskStatus(TaskStatus.DONE);
            return;
        } else if (!allIsNew) {
            setTaskStatus(TaskStatus.IN_PROGRESS);
            return;
        }
        setTaskStatus(TaskStatus.NEW);
    }

    // Получаем копию субтасков
    public HashMap<Integer, Subtask> getCopyOfSubtasks() {
        HashMap<Integer, Subtask> copy = new HashMap<>();
        for (int key : subtasks.keySet()) {
            copy.put(key, new Subtask(subtasks.get(key)));
        }
        return copy;
    }

    // Устанавливаем время окончания эпика вручную(нужно для десереализации из файла)
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    // Вовзращаем дату окончания Эпика - дату задачи, которая предполагается завершиться позже всех
    @Override
    public LocalDateTime getEndTime() {
        List<Subtask> subtasksList = subtasks.values().stream().toList();
        if (!subtasksList.isEmpty()) {
            List<Subtask> sortedSubtasks = subtasksList.stream()
                    .filter(s -> s.taskStatus != TaskStatus.DONE)
                    .sorted(Comparator.comparing(s -> s.getEndTime()))
                    .collect(Collectors.toList());
            return sortedSubtasks.getLast().getEndTime();
        }
        return null;
    }

    // Устанавливаем время начала эпика вручную(нужно для десереализации из файла)
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // Вовзращаем дату начала Эпика - дату задачи, которая начинается всех раньше
    public LocalDateTime getStartTime() {
        List<Subtask> subtasksList = subtasks.values().stream().toList();
        if (!subtasksList.isEmpty()) {
            List<Subtask> sortedSubtasks = subtasksList.stream()
                    .filter(s -> s.taskStatus != TaskStatus.DONE)
                    .sorted(Comparator.comparing(s -> s.getEndTime()))
                    .collect(Collectors.toList());
            return sortedSubtasks.getFirst().startTime;
        }
        return null;
    }

    // Получаем продолжительность эпика, исходя из предполагаемой продолжительности всех задач
    public int getDuration() {
        long sum = subtasks.values().stream().filter(s -> s.taskStatus != TaskStatus.DONE)
                .map(s -> s.duration.toMinutes()).reduce(0L, (a, b) -> a + b);
        return (int) sum;

    }


    @Override
    public String toString() {
        return String
                .format("%d, %s, %s, %s, %s, %s, %d, %s",
                        getTaskId(), TaskType.EPIC, getName(), getTaskStatus(), getDescription(),
                        getStartTime() != null ? getStartTime().format(dateTimeFormatter) : null,
                        getDuration(),
                        getEndTime() != null ? getEndTime().format(dateTimeFormatter) : endTime);
    }
}
