package ru.yandex.todo.service;

import ru.yandex.todo.model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    protected static final int historySize = 10; // Размер массива для хранения истории

    private final ArrayList<Task> taskHistory = new ArrayList<>(); // Храним историю просмотра задач


    // Добавляем таску в конец истории, проверяя, что массив ещё не заполнен, если заполнен,
    // то заменяем нулевой элемент
    @Override
    public void add(Task task) {

        if (taskHistory.size() < historySize) {
            taskHistory.add(task);
        } else {
            taskHistory.add(0, task);
            taskHistory.remove(taskHistory.size() - 1);
        }
    }

    // Возвращаем список с историей
    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory;
    }
}
