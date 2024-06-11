package ru.yandex.todo.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.todo.exceptions.DurationAdapterException;
import ru.yandex.todo.exceptions.ManagerAddTaskException;
import ru.yandex.todo.exceptions.ManagerCrossTimeException;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

import static ru.yandex.todo.server.HttpTaskServer.subtaskPath;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    // Хэндлер для обработки путей к сабтаскам

    public SubtasksHandler(TaskManager manager) {
        super(manager);

    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            final String path = exchange.getRequestURI().getPath();
            final String method = exchange.getRequestMethod();

            switch (method) {
                case "GET": {
                    if (isValidPath("^" + subtaskPath + "$", path)) {
                        String stringResponse = gson.toJson(manager.getAllSubtasks());
                        writeResponse(exchange, stringResponse, 200);
                    } else if (isValidPath("^" + subtaskPath + "/\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst(subtaskPath + "/", ""));
                        if (id != -1 && manager.hasTask(id)) {
                            String stringResponse = gson.toJson(manager.getTaskById(id));
                            writeResponse(exchange, stringResponse, 200);
                        } else {
                            writeResponse(exchange, "Неверный id: " + id + " " + method, 404);
                        }
                    } else {
                        writeResponse(exchange, "Такого пути не существует: " + path + " " + method, 405);
                    }

                    break;
                }
                case "POST": {
                    if (isValidPath("^" + subtaskPath + "$", path)) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                        try {
                            Optional<Subtask> subtaskOptional = Optional.of(gson.fromJson(body, Subtask.class));
                            if (subtaskOptional.isEmpty()) {
                                writeResponse(exchange, "не верные поля для создания подзадачи", 406);
                            }
                            Subtask task = subtaskOptional.get();
                            checkTaskFields(task);
                            // Получаем эпик для добавления подзадачи
                            Epic epic = (Epic) manager.getTaskById(task.getEpicId());
                            manager.addTask(new Subtask(epic, task.getName(), task.getDescription(),
                                    task.getStartTime(), task.getDuration()));
                        } catch (ManagerAddTaskException e) {
                            writeResponse(exchange, e.getMessage(), 406);
                        } catch (ManagerCrossTimeException e) {
                            writeResponse(exchange, e.getMessage(), 406);
                        } catch (JsonSyntaxException e) {
                            writeResponse(exchange, "Неверный формат Json", 406);
                        } catch (Exception e) {
                            writeResponse(exchange, "Internal Server Error: " + e.getMessage(), 500);
                        }

                    } else if (isValidPath("^" + subtaskPath + "\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst(subtaskPath + "/", ""));
                        if (id != -1 && manager.hasTask(id)) {
                            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                            try {
                                JsonObject jsonElement = JsonParser.parseString(body).getAsJsonObject();
                                String newName = jsonElement.get("name").getAsString();
                                String newDescription = jsonElement.get("description").getAsString();
                                Subtask task = (Subtask) manager.getTaskById(id);
                                manager.updateTask(id, newName != null ? newName : task.getName(),
                                        newDescription != null ? newDescription : task.getDescription());
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
                            writeResponse(exchange, "id:" + id + " не найден", 404);
                        }
                    } else {
                        writeResponse(exchange, "Такого пути не существует: " + path + " " + method, 405);
                    }
                    writeResponse(exchange, "", 201);
                    break;
                }
                case "DELETE": {
                    if (isValidPath("^" + subtaskPath + "/\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst(subtaskPath + "/", ""));
                        if (id != -1 && manager.hasTask(id)) {
                            manager.delTaskById(id);
                            writeResponse(exchange, "Подзадача с id:" + id + " удалена " + method, 200);
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
        } catch (DurationAdapterException e) {
            writeResponse(exchange, e.getMessage(), 406);
        } catch (Exception e) {
            writeResponse(exchange, "Internal Server Error: " + e.getMessage(), 500);
        }

    }

    // Проверяем наличие обязательных полей при добавлении задачи
    private void checkTaskFields(Subtask task) {
        if (task.getName() == null || task.getDescription() == null || task.getStartTime() == null
                || task.getDuration() == 0 || task.getEpicId() <= 0) {
            throw new ManagerAddTaskException("Не все обязательные поля переданы");
        } else if (!manager.hasTask(task.getEpicId()) ||
                manager.getTaskById(task.getEpicId()).getClass() != Epic.class) {
            throw new ManagerAddTaskException("Не существует такого эпика с id: " + task.getEpicId() + " " +
                    ", в который вы хотите добавить подзадачу");
        }
    }


}
