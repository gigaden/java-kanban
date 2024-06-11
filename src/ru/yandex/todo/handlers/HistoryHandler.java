package ru.yandex.todo.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.todo.service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

import static ru.yandex.todo.server.HttpTaskServer.historyPath;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    // Обрабатываем получение историй задач

    public HistoryHandler(TaskManager manager) {
        super(manager);

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            final String path = exchange.getRequestURI().getPath();
            final String method = exchange.getRequestMethod();

            switch (method) {
                case "GET": {
                    if (Pattern.matches("^" + historyPath + "$", path)) {
                        String stringResponse = gson.toJson(manager.getHistory());
                        writeResponse(exchange, stringResponse, 200);
                    } else {
                        writeResponse(exchange, "Такого пути не существует: " + path + " " + method, 405);
                    }

                    break;
                }
                default: {
                    writeResponse(exchange, "Метод не поддерживается: " + method, 405);
                }
            }
        } catch (Exception e) {
            writeResponse(exchange, "Internal Server Error: " + e.getMessage(), 500);
        }
    }
}
