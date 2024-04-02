package ru.yandex.todo.service;
import ru.yandex.todo.model.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);

    ArrayList<Task> getHistory();

}
