package ru.yandex.todo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.todo.exceptions.ManagerCrossTimeException;
import ru.yandex.todo.exceptions.ManagerSaveException;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager fileBackedTaskManager;
    private File file;
    private LocalDateTime localDateTime;

    @BeforeEach
    public void beforeEach() throws IOException {
        file = File.createTempFile("database", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(file);
        localDateTime = LocalDateTime.now();
    }

    @Test
    public void shouldBePositiveWhenFileIsNotEmptyAfterAddedTaskToManager() {
        Assertions.assertEquals(0, file.length());
        Task task = new Task("task1", "descr", localDateTime, 60);
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask = new Subtask(epic, "subtask1", "descr", localDateTime.plusDays(1), 180);
        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addTask(epic);
        fileBackedTaskManager.addTask(subtask);
        Assertions.assertNotEquals(0, file.length(), "Файл пустой");
    }

    @Test
    public void shouldBePositiveWhenTasksUploadedFromFileToManager() {
        Task task = new Task("task1", "descr", localDateTime, 200);
        Epic epic = new Epic("epic1", "descr");
        Subtask subtask = new Subtask(epic, "subtask1", "descr", localDateTime.plusDays(1), 100);
        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addTask(epic);
        fileBackedTaskManager.addTask(subtask);
        int allTasksLength = fileBackedTaskManager.getAllTasks().size();

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
        int allTasksLengthFromNewManager = newManager.getAllTasks().size();

        Assertions.assertEquals(allTasksLength, allTasksLengthFromNewManager,
                "Таски не загрузились в новый менеджер");

    }

    // Проверяем выброс исключения при передачи null вместо файла
    @Test
    public void shouldBePositiveWhenAddedNullFileToManager() {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            new FileBackedTaskManager(null);
        }, "Попытка прочитать null вместо файла должна приводить к ошибке");
    }

    // Проверяем выброс исключения при передаче не корректного пути файла
    @Test
    public void shouldBePositiveWhenAddeWrongWayToFile() {
        Assertions.assertThrows(ManagerSaveException.class, () ->
                        FileBackedTaskManager.loadFromFile(new File("C://windows/system32")),
                "Попытка прочитать несуществующий файл должна приводить к ошибке");
    }

    // Проверяем выброс исключения при добавлении задачи, пересекающейся с другими
    @Test
    public void shouldBePositiveWhenAddedCrossingTask() {
        Assertions.assertThrows(ManagerCrossTimeException.class, () -> {
            fileBackedTaskManager
                    .addTask(new Task("task50", "descr", localDateTime.plusMonths(1), 60));
            fileBackedTaskManager
                    .addTask(new Task("task50", "descr", localDateTime.plusMonths(1), 60));
        }, "При попытке добавить задачу, пересекающуюся с существующими должна быть ошибка");
    }

    // Проверяем, что задача с незаданной датой начала не попадает в трисет
    @Test
    public void shouldBePositiveWhenAddedTaskWithoutStartTime() {
        int sizeOftreeset = fileBackedTaskManager.getPrioritizedTasks().size();
        fileBackedTaskManager
                .addTask(new Task("task80", "descr80", null, 60));
        int sizeAfterAddedTask = fileBackedTaskManager.getPrioritizedTasks().size();
        Assertions.assertEquals(sizeOftreeset, sizeAfterAddedTask,
                "Задача без времения начала не должна попасть в трисет");
    }

    // Проверяем расчёт времени начала и окончания эпика
    @Test
    public void shouldBePositiveWhenCalculatingStartAndEndTimeOfEpic() {
        Epic epic = new Epic("epic", "description");
        fileBackedTaskManager.addTask(epic);
        Subtask subtask = new Subtask(epic, "subtask", "description", localDateTime, 60);
        Subtask subtask1 = new Subtask(epic, "subtask1", "description", localDateTime.plusDays(1), 60);
        Subtask subtask2 = new Subtask(epic, "subtask2", "description", localDateTime.minusDays(1), 60);
        fileBackedTaskManager.addTask(subtask);
        fileBackedTaskManager.addTask(subtask1);
        fileBackedTaskManager.addTask(subtask2);
        Assertions.assertEquals(epic.getStartTime(), subtask2.getStartTime(), "Неверно расчитано время старта эпика");
        Assertions.assertEquals(epic.getEndTime(), subtask1.getEndTime(), "Неверно расчитано время окончания эпика");
    }
}
