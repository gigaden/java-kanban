package ru.yandex.todo.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.todo.model.Task;
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
import static ru.yandex.todo.server.HttpTaskServer.taskPath;

public class TasksHandlerTest {

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

    @Test // Проверяем добавление задачи
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 1", "Testing task 1",
                localDateTime, 60);
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + taskPath);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertNotNull(response.body(), "Получено пустое тело");

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getAllTasks();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
        client.close();
    }

    @Test // Проверяем получения задач через гет
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 1",
                localDateTime, 60);
        Task task2 = new Task("Test 2", "Testing task 2",
                localDateTime.plusDays(1), 60);
        manager.addTask(task);
        manager.addTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + taskPath);
        HttpRequest requestOne = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> responseOne = client.send(requestOne, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(200, responseOne.statusCode());
        Assertions.assertNotNull(responseOne.body(), "Получено пустое тело");

        Type type = new TypeToken<List<Task>>() {}.getType();
        List<Task> tasks = gson.fromJson(responseOne.body(), type);

        Assertions.assertNotNull(tasks, "Задачи не возвращаются");
        Assertions.assertEquals(2, tasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("Testing task 1", tasks.getFirst().getDescription(),
                "Некорректное имя задачи");

        // Проверяем получение задачи по id
        URI url2 = URI.create(mainPath + PORT + taskPath +"/2");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, responseTwo.statusCode());
        Assertions.assertNotNull(responseTwo.body(), "Получено пустое тело");
        Task taskTwo = gson.fromJson(responseTwo.body(), Task.class);
        Assertions.assertEquals(taskTwo.getDescription(), "Testing task 2", "Описание задач не совпадает");
        client.close();
    }

    @Test // Проверяем удаление задачи
    public void testDeleteTask() throws IOException, InterruptedException {

        Task task = new Task("Test 3", "Testing task 1",
                localDateTime, 60);
        manager.addTask(task);
        Assertions.assertEquals(1, manager.getAllTasks().size(), "Задача не добавилась");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + taskPath + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(response.body(), "Получено пустое тело");
        Assertions.assertEquals(0, manager.getAllTasks().size(), "Задача не удалилась");
        client.close();
    }

}
