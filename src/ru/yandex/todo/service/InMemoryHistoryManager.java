package ru.yandex.todo.service;

import ru.yandex.todo.model.Task;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    protected static final int historySize = 10; // Размер массива для хранения истории

    private final List<Task> taskHistory = new LinkedList<>(); // Храним историю просмотра задач


    // Добавляем таску в конец истории, проверяя, что массив ещё не заполнен, если заполнен,
    // то заменяем нулевой элемент
    @Override
    public void add(Task task) {

        if (task != null) {
            if (taskHistory.size() < historySize) {
                taskHistory.add(task);
            } else {
                taskHistory.add(0, task);
                taskHistory.remove(taskHistory.size() - 1);
            }
        }
    }

    // Возвращаем список с историей

    @Override
    public List<Task> getHistory() {
        return taskHistory;
    }
}
