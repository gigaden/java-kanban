package ru.yandex.todo.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.server.HttpTaskServer;
import ru.yandex.todo.service.Managers;
import ru.yandex.todo.service.TaskManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static ru.yandex.todo.server.HttpTaskServer.mainPath;
import static ru.yandex.todo.server.HttpTaskServer.subtaskPath;

public class SubtasksHandlerTest {

    private TaskManager manager;
    private LocalDateTime localDateTime;
    private HttpTaskServer server;
    private Gson gson;
    private static final int PORT = 8080;


    @BeforeEach
    public void beforeEach() throws IOException {
        manager = Managers.getDefault();
        gson = Managers.getGson();
        server = new HttpTaskServer(manager);
        server.start();
        localDateTime = LocalDateTime.now();
    }

    @AfterEach
    public void afterEach() {
        server.stop();
    }

    @Test // Проверяем добавление подзадачи
    public void testAddSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("epic", "epic description");
        manager.addTask(epic);
        Subtask task = new Subtask(epic, "Test 1", "Testing task 1",
                localDateTime, 60);
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + subtaskPath);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getAllSubtasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        client.close();
    }

    @Test // Проверяем получения подзадач через гет
    public void testGetSubTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("epic", "descr");
        Subtask task = new Subtask(epic, "Test 2", "Testing task 1",
                localDateTime, 60);
        Subtask task2 = new Subtask(epic, "Test 2", "Testing task 2",
                localDateTime.plusDays(1), 60);
        manager.addTask(epic);
        manager.addTask(task);
        manager.addTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + subtaskPath);
        HttpRequest requestOne = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> responseOne = client.send(requestOne, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(200, responseOne.statusCode());


        // проверяем, что создалась одна задача с корректным именем
        Type type = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> tasks = gson.fromJson(responseOne.body(), type);

        Assertions.assertNotNull(tasks, "Задачи не возвращаются");
        Assertions.assertEquals(2, tasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("Testing task 1", tasks.getFirst().getDescription(),
                "Некорректное имя задачи");

        // Проверяем получение задачи по id
        URI url2 = URI.create(mainPath + PORT + subtaskPath + "/3");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, responseTwo.statusCode());
        Subtask taskTwo = gson.fromJson(responseTwo.body(), Subtask.class);
        Assertions.assertEquals(taskTwo.getDescription(), "Testing task 2", "Описание задач не совпадает");
        client.close();
    }

    @Test // Проверяем удаление подзадачи
    public void testDeleteTask() throws IOException, InterruptedException {

        Epic epic = new Epic("epic", "descr");
        Subtask task = new Subtask(epic, "Test 3", "Testing task 1",
                localDateTime, 60);
        manager.addTask(epic);
        manager.addTask(task);
        Assertions.assertEquals(2, manager.getAllTasks().size(), "Задача не добавилась");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + subtaskPath + "/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertEquals(1, manager.getAllTasks().size(), "Задача не удалилась");
        client.close();
    }

}
