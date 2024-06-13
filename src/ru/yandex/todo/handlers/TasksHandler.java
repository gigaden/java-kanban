package ru.yandex.todo.handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.todo.exceptions.DurationAdapterException;
import ru.yandex.todo.exceptions.ManagerAddTaskException;
import ru.yandex.todo.exceptions.ManagerCrossTimeException;
import ru.yandex.todo.model.Task;
import ru.yandex.todo.service.TaskManager;

import java.io.IOException;
import java.util.Optional;

import static ru.yandex.todo.server.HttpTaskServer.taskPath;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    // Хэндлер для обработки путей к таскам

    public TasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            final String path = exchange.getRequestURI().getPath();
            final String method = exchange.getRequestMethod();

            switch (method) {
                case "GET": {
                    if (isValidPath("^" + taskPath + "$", path)) {
                        String stringResponse = gson.toJson(manager.getAllTasks());
                        writeResponse(exchange, stringResponse, 200);
                    } else if (isValidPath("^" + taskPath + "/\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst(taskPath + "/", ""));
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
                    if (isValidPath("^" + taskPath + "?$", path)) {
                        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                        try {
                            Optional<Task> taskOptional = Optional.of(gson.fromJson(body, Task.class));
                            if (taskOptional.isEmpty()) {
                                writeResponse(exchange, "не верные поля для создания задачи", 406);
                            }
                            Task task = taskOptional.get();
                            checkTaskFields(task);
                            manager.addTask(new Task(task.getName(), task.getDescription(),
                                    task.getStartTime(), task.getDuration()));
                        } catch (ManagerAddTaskException e) {
                            writeResponse(exchange, "Не все обязательные поля переданы", 406);
                        } catch (ManagerCrossTimeException e) {
                            writeResponse(exchange, e.getMessage(), 406);
                        } catch (JsonSyntaxException e) {
                            writeResponse(exchange, "Неверный формат Json", 406);
                        } catch (Exception e) {
                            writeResponse(exchange, "Internal Server Error: " + e.getMessage(), 500);
                        }

                    } else if (isValidPath("^" + taskPath + "\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst(taskPath + "/", ""));
                        if (id != -1 && manager.hasTask(id)) {
                            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                            try {
                                Task task = gson.fromJson(body, Task.class);
                                checkTaskFields(task);
                                manager.updateTask(id, task.getName(), task.getDescription());
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
                    if (isValidPath("^" + taskPath + "/\\d+$", path)) {
                        int id = parsePathId(path.replaceFirst(taskPath + "/", ""));
                        if (id != -1 && manager.hasTask(id)) {
                            manager.delTaskById(id);
                            writeResponse(exchange, "Задача с id:" + id + " удалена " + method, 200);
                        } else {
                            writeResponse(exchange, "Неверный id: " + id + " " + method, 404);
                        }

                    } else {
                        writeResponse(exchange, "Такого пути не существует: " + path + " " + method, 405);
                    }
                    break;

                }
                default: {
                    writeResponse(exchange, "Данный метод не поддерживается: " + method, 405);
                }
            }
        } catch (DurationAdapterException e) {
            writeResponse(exchange, e.getMessage(), 406);
        } catch (Exception e) {
            writeResponse(exchange, "Internal Server Error: " + e.getMessage(), 500);
        }

    }

    // Проверяем наличие обязательных полей при добавлении задачи
    private void checkTaskFields(Task task) {
        if (task.getName() == null || task.getDescription() == null || task.getStartTime() == null
                || task.getDuration() == 0) {
            throw new ManagerAddTaskException("Не все обязательные поля переданы");
        }
    }


}
