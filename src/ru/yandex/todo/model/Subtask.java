package ru.yandex.todo.model;

import ru.yandex.todo.service.InMemoryTaskManager;
import ru.yandex.todo.service.TaskStatus;

public class Subtask extends Task {
    // Класс для описания подзадач

    private final Epic epic;

    public Subtask(Epic epic, String name, String description) {
        this.epic = epic;
        this.name = name;
        this.description = description;
        taskStatus = TaskStatus.NEW;
        taskId = InMemoryTaskManager.getTaskId();
        InMemoryTaskManager.setTaskId();
    }

    // Конструктор для глубокого копирования
    public Subtask(Subtask another) {
        this.taskId = another.taskId;
        this.name = another.name;
        this.description = another.description;
        this.taskStatus = another.taskStatus;
        this.epic = ((Subtask) another).epic;

    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public void setTaskStatus(TaskStatus taskStatus) {
        // при смене статуса на DONE запускаем проверку остальных статусов подзадач в эпике
        // если все подзадачи DONE, то меняем статус эпика на DONE
        this.taskStatus = taskStatus;
        epic.checkSubtasksStatusDone();

    }


    @Override
    public String toString() {
        return String.format("Subtask id= %d | имя: %s\n" +
                "описание: %s\n" +
                "эпик: id= %d  имя: %s\n" +
                "статус: %s\n", getTaskId(), getName(), getDescription(), epic.getTaskId(), epic.getName(), taskStatus);
    }

}
