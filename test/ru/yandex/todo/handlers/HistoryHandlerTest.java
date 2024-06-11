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
import java.util.Objects;

import static ru.yandex.todo.server.HttpTaskServer.*;

public class HistoryHandlerTest {

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

    @Test // Проверяем добавление задач в историю
    public void testAddTaskToHistory() throws IOException, InterruptedException {
        Task task = new Task("name1", "descr", localDateTime, 60);
        Task task2 = new Task("name2", "descr", localDateTime.plusDays(1), 60);
        manager.addTask(task);
        manager.addTask(task2);
        Assertions.assertEquals(0, manager.getHistory().size(), "История не пуста.");
        // Получаем задачу через api
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + taskPath + "/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Неверный код ответа сервера");

        Assertions.assertEquals(1, manager.getHistory().size(), "История пуста.");
        client.close();
    }

    @Test // Проверяем получение задач из истории
    public void testGetTaskFromHistory() throws IOException, InterruptedException {
        Task task = new Task("name1", "descr", localDateTime, 60);
        Task task2 = new Task("name2", "descr", localDateTime.plusDays(1), 60);
        manager.addTask(task);
        manager.addTask(task2);
        manager.getTaskById(task2.getTaskId());
        manager.getTaskById(task.getTaskId());
        Assertions.assertEquals(2, manager.getHistory().size(), "История пуста.");
        // Получаем задачу через api
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + historyPath);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Неверный код ответа сервера");

        Type type = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), type);

        Assertions.assertEquals(2, tasks.size(), "Размер истории не совпадает.");
        boolean tasksIsEquals = Objects.equals(tasks.getFirst().getName(), manager.getHistory().getFirst().getName());
        Assertions.assertTrue(tasksIsEquals, "Задачи не совпадают");
        client.close();
    }


}
