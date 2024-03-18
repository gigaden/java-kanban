package model;
import service.TaskManager;
import service.TaskStatus;

import java.util.HashMap;

public class Epic extends Task {
    // Класс для создания эпика


    public Epic(String name, String description) {
        this.name = name;
        this.description = description;
        taskStatus = TaskStatus.NEW;
        subtasks = new HashMap<>();
        taskId = TaskManager.getTaskId();
        TaskManager.setTaskId();
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
            System.out.printf("Подзадача с id %d удалена\n", id);
        } else {
            System.out.println("Подзадачи с таким id нет");
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


    @Override
    public String toString() {
        return String.format("model.Epic id= %d | имя: %s\n" +
                "описание: %s\n" +
                "статус: %s\n" +
                "подзадач всего: %d\n" +
                "подзадачи: %s", getTaskId(), getName(), getDescription(), getTaskStatus(), subtasks.size(), getSubtasks());
    }
}
