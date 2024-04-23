package ru.yandex.todo.service;

import ru.yandex.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {

    void add(Task task); // Добавляем задачу в историю

    List<Task> getHistory(); // Получаем список истории

    void remove(int id); // Удаляем задачу из истории

    void clearAll(); // Очищаем историю

}
