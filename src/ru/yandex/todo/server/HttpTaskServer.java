package ru.yandex.todo.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.todo.handlers.*;
import ru.yandex.todo.service.Managers;
import ru.yandex.todo.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    // Класс для управления сервером и эндпоинтами
    protected static int PORT = 8080;
    final static int BACKLOG = 0;
    private HttpServer server;
    private TaskManager manager;
    // Прописываем пути для api
    public static final String mainPath = "http://localhost:";
    public static final String subtaskPath = "/api/v1/subtasks";
    public static final String taskPath = "/api/v1/tasks";
    public static final String epicPath = "/api/v1/epics";
    public static final String historyPath = "/api/v1/history";
    public static final String prioritizedPath = "/api/v1/prioritized";

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;

        //createTasks(this.manager);
        //createEpics(this.manager);

        server = HttpServer.create(new InetSocketAddress(PORT), BACKLOG);
        server.createContext(taskPath, new TasksHandler(manager));
        server.createContext(subtaskPath, new SubtasksHandler(manager));
        server.createContext(epicPath, new EpicsHandler(manager));
        server.createContext(historyPath, new HistoryHandler(manager));
        server.createContext(prioritizedPath, new PrioritizedHandler(manager));
    }

    public void start() {
        System.out.println("Started TaskServer " + PORT);
        System.out.println(mainPath + PORT + taskPath);
        System.out.println(mainPath + PORT + subtaskPath);
        System.out.println(mainPath + PORT + epicPath);
        System.out.println(mainPath + PORT + historyPath);
        System.out.println(mainPath + PORT + prioritizedPath);
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Stopped server on port: " + PORT);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
        // server.stop();
    }


}
