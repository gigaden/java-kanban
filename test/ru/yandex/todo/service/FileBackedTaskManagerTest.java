package ru.yandex.todo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager fileBackedTaskManager;
    private File file;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("database", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(file);
    }

    @Test
    public void shouldBePositiveWhenFileIsNotEmptyAfterAddedTaskToManager() {
        Assertions.assertEquals(0, file.length());
        Task task = new Task("task1", "descr");
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask = new Subtask(epic, "subtask1", "descr");
        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addTask(epic);
        fileBackedTaskManager.addTask(subtask);
        Assertions.assertNotEquals(0, file.length(), "Файл пустой");
    }

    @Test
    public void shouldBePositiveWhenTasksUploadedFromFileToManager() {
        Task task = new Task("task1", "descr");
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask = new Subtask(epic, "subtask1", "descr");
        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addTask(epic);
        fileBackedTaskManager.addTask(subtask);
        int allTasksLength = fileBackedTaskManager.getAllTasks().size();

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
        int allTasksLengthFromNewManager = newManager.getAllTasks().size();

        Assertions.assertEquals(allTasksLength, allTasksLengthFromNewManager,
                "Таски не загрузились в новый менеджер");

    }
}
