package ru.yandex.todo.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import ru.yandex.todo.service.Managers;
import ru.yandex.todo.service.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class BaseHttpHandler {
    // Класс с повторяющимися методами для хэндлеров

    protected final TaskManager manager;
    protected final Gson gson;
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = Managers.getGson();
    }


    // Отправляем ответ сервера
    protected void writeResponse(HttpExchange exchange, String response, int code) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(code, 0);
            os.write(response.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    // Проверяем id
    protected int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Проверяем путь
    protected boolean isValidPath(String pattern, String path) {
        return Pattern.matches(pattern, path);
    }
}
