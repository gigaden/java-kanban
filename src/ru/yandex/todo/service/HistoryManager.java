package ru.yandex.todo.service;
import ru.yandex.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

}
