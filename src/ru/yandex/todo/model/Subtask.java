package ru.yandex.todo.model;

import ru.yandex.todo.service.InMemoryTaskManager;
import ru.yandex.todo.service.TaskStatus;
import ru.yandex.todo.service.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    // Класс для описания подзадач

    private final transient Epic epic;
    private final int epicId;

    public Subtask(Epic epic) {
        this.epic = epic;
        this.epicId = epic.getTaskId();
    }

    public Subtask(Epic epic, String name, String description, LocalDateTime startTime, int duration) {
        this.epic = epic;
        this.name = name;
        this.description = description;
        taskStatus = TaskStatus.NEW;
        taskId = InMemoryTaskManager.getTaskId();
        InMemoryTaskManager.setTaskId();
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
        this.epicId = epic.getTaskId();
    }

    // Конструктор для глубокого копирования
    public Subtask(Subtask another) {
        this.taskId = another.taskId;
        this.name = another.name;
        this.description = another.description;
        this.taskStatus = another.taskStatus;
        this.epic = ((Subtask) another).epic;
        this.epicId = another.epic.getTaskId();

    }

    public Epic getEpic() {
        return epic;
    }

    public int getEpicId() {
        return epicId;
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

        return String.format("%d, %s, %s, %s, %s, %d, %s, %s, %s",
                getTaskId(), TaskType.SUBTASK, getName(), getTaskStatus(), getDescription(), epic.getTaskId(),
                startTime.format(dateTimeFormatter), duration.toMinutes(), getEndTime().format(dateTimeFormatter));
    }

}
