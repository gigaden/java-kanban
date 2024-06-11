package ru.yandex.todo.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.todo.exceptions.ManagerAddTaskException;
import ru.yandex.todo.exceptions.ManagerCrossTimeException;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.service.TaskManager;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

import static ru.yandex.todo.server.HttpTaskServer.epicPath;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    // Хэндлер для обработки путей к эпикам

    public EpicsHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            final String path = exchange.getRequestURI().getPath();
            final String method = exchange.getRequestMethod();

            switch (method) {
                case "GET": {
                    if (Pattern.matches("^" + epicPath + "$", path)) {
                        String stringResponse = gson.toJson(manager.getAllEpics());
                        writeResponse(exchange, stringResponse, 200);
                    } else if (Pattern.matches("^" + epicPath + "/\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst(epicPath + "/", ""));
                        if (id != -1 && manager.hasTask(id)) {
                            String stringResponse = gson.toJson(manager.getTaskById(id));
                            writeResponse(exchange, stringResponse, 200);
                        } else {
                            writeResponse(exchange, "Неверный id: " + id + " " + method, 404);
                        }
                    } else if (Pattern.matches("^" + epicPath + "/\\d+/subtasks$", path)) {
                        int id = parsePathId(path.replaceFirst(epicPath + "/", "")
                                .replaceFirst("/subtasks", ""));
                        if (id != -1 && manager.hasTask(id)) {
                            String stringResponse = gson.toJson(manager.getAllSubtasksById(id));
                            writeResponse(exchange, stringResponse, 200);
                        } else {
                            writeResponse(exchange, "Неверный id эпика: " + id + " " + method, 404);
                        }
                    } else {
                        writeResponse(exchange, "Такого пути не существует: " + path + " " + method, 405);
                    }

                    break;
                }
                case "POST": {
                    if (Pattern.matches("^" + epicPath + "$", path)) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                        Optional<Epic> epicOptional = null;
                        try {
                            JsonElement jsonElement = JsonParser.parseString(body);
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            String name = jsonObject.get("name").getAsString();
                            String description = jsonObject.get("description").getAsString();
                            Epic task = new Epic(name, description);
                            checkTaskFields(task);
                            manager.addTask(new Epic(task.getName(), task.getDescription()));
                        } catch (ManagerAddTaskException e) {
                            writeResponse(exchange, e.getMessage(), 406);
                        } catch (ManagerCrossTimeException e) {
                            writeResponse(exchange, e.getMessage(), 406);
                        } catch (JsonSyntaxException e) {
                            writeResponse(exchange, "Неверный формат Json", 406);
                        } catch (Exception e) {
                            writeResponse(exchange, "Internal Server Error: " + e.getMessage(), 500);
                        }

                    } else {
                        writeResponse(exchange, "Такого пути не существует: " + path + " " + method, 405);
                    }
                    writeResponse(exchange, "", 201);
                    break;
                }
                case "DELETE": {
                    if (Pattern.matches("^" + epicPath + "/\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst(epicPath + "/", ""));
                        if (id != -1 && manager.hasTask(id)) {
                            manager.delTaskById(id);
                            writeResponse(exchange, "Эпик с id:" + id + " удалена " + method, 200);
                        } else {
                            writeResponse(exchange, "Неверный id: " + id + " " + method, 404);
                        }

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

    // Проверяем наличие обязательных полей при добавлении задачи
    private void checkTaskFields(Epic task) {
        if (task.getName() == null || task.getDescription() == null) {
            throw new ManagerAddTaskException("Не все обязательные поля переданы");
        }
    }

}
