package ru.yandex.todo.service;

import ru.yandex.todo.model.Task;

public class Node {
    // Узел нашего двусвязного списка

    public Task task;
    public Node next;
    public Node prev;

    public Node(Task task) {
        this.task = task;
    }

}
