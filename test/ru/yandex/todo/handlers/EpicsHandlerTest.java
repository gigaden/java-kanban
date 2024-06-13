package ru.yandex.todo.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
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

import static ru.yandex.todo.server.HttpTaskServer.epicPath;
import static ru.yandex.todo.server.HttpTaskServer.mainPath;

public class EpicsHandlerTest {

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

    @Test // Проверяем добавление эпика
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 1", "Testing task 1");
        // конвертируем её в JSON
        String taskJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + epicPath);
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(201, response.statusCode());
        Assertions.assertNotNull(response.body(), "Получено пустое тело");

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getAllEpics();

        Assertions.assertNotNull(tasksFromManager, "Задачи не возвращаются");
        Assertions.assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        Assertions.assertEquals("Test 1", tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
        client.close();
    }

    @Test // Проверяем получения задач через гет
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic("Test 2", "Testing task 1");
        Epic epic2 = new Epic("Test 2", "Testing task 2");
        manager.addTask(epic);
        manager.addTask(epic2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + epicPath);
        HttpRequest requestOne = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> responseOne = client.send(requestOne, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        Assertions.assertEquals(200, responseOne.statusCode());
        Assertions.assertNotNull(responseOne.body(), "Получено пустое тело");


        // проверяем, что создалась одна задача с корректным именем
        Type type = new TypeToken<List<Epic>>() {}.getType();
        System.out.println(responseOne.body());
        List<Epic> tasks = gson.fromJson(responseOne.body(), type);

        Assertions.assertNotNull(tasks, "Задачи не возвращаются");
        Assertions.assertEquals(2, tasks.size(), "Некорректное количество задач");
        Assertions.assertEquals("Testing task 1", tasks.getFirst().getDescription(),
                "Некорректное имя задачи");

        // Проверяем получение задачи по id
        URI url2 = URI.create(mainPath + PORT + epicPath +"/2");
        HttpRequest requestTwo = HttpRequest.newBuilder().uri(url2).GET().build();
        HttpResponse<String> responseTwo = client.send(requestTwo, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, responseTwo.statusCode());
        Assertions.assertNotNull(responseTwo.body(), "Получено пустое тело");
        Task taskTwo = gson.fromJson(responseTwo.body(), Epic.class);
        Assertions.assertEquals(taskTwo.getDescription(), "Testing task 2", "Описание задач не совпадает");
        client.close();
    }

    @Test // Проверяем удаление задачи
    public void testDeleteTask() throws IOException, InterruptedException {

        Epic epic = new Epic("Test 3", "Testing task 1");
        manager.addTask(epic);
        Assertions.assertEquals(1, manager.getAllEpics().size(), "Задача не добавилась");

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + epicPath + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Assertions.assertEquals(200, response.statusCode());
        Assertions.assertNotNull(response.body(), "Получено пустое тело");
        Assertions.assertEquals(0, manager.getAllEpics().size(), "Задача не удалилась");
        client.close();
    }

    @Test // Проверяем получение подзадач
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 3", "Testing task 1");
        manager.addTask(epic);
        Subtask subtask = new Subtask(epic, "subtask", "descr", localDateTime, 60 );
        manager.addTask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + epicPath + "/1" + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type type = new TypeToken<List<Subtask>>() {}.getType();
        List<Subtask> subtasks = gson.fromJson(response.body(), type);

        Assertions.assertEquals(1, subtasks.size(), "Неверное количество подзадач вернулось");
        client.close();
    }

}
