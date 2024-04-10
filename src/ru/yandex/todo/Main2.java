package ru.yandex.todo;
import ru.yandex.todo.model.*;
import ru.yandex.todo.service.Managers;
import ru.yandex.todo.service.TaskManager;

public class Main2 {
    public static void main(String[] args) {
        Epic epic = new Epic("Hello", "World");
        TaskManager taskManager = Managers.getDefault();
        taskManager.addTask(epic);
        System.out.println(taskManager.getAllSimpleTasks());
    }
}
