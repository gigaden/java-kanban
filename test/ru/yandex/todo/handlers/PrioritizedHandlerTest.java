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
import static ru.yandex.todo.server.HttpTaskServer.prioritizedPath;

public class PrioritizedHandlerTest {

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

    @Test // Проверяем получение задач из истории
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task = new Task("name1", "descr", localDateTime, 60);
        Task task2 = new Task("name2", "descr", localDateTime.plusDays(1), 60);
        Task task3 = new Task("name3", "descr", localDateTime.minusDays(1), 60);
        manager.addTask(task);
        manager.addTask(task2);
        manager.addTask(task3);
        Assertions.assertEquals(3, manager.getPrioritizedTasks().size(), "Задачи не добавлены.");
        // Получаем задачу через api
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(mainPath + PORT + prioritizedPath);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Неверный код ответа сервера");
        Assertions.assertNotNull(response.body(), "Получено пустое тело");

        Type type = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasks = gson.fromJson(response.body(), type);

        Assertions.assertEquals(3, tasks.size(), "Размер мапы с задачами неверный.");
        boolean tasksIsEquals = tasks.getFirst().getName().equals(task3.getName()) &&
                tasks.getLast().getName().equals(task2.getName());
        Assertions.assertTrue(tasksIsEquals, "Задачи не совпадают");
        client.close();
    }


}
