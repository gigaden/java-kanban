package ru.yandex.todo.service;

import ru.yandex.todo.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final HistoryLinkedList taskHistory = new HistoryLinkedList(); // Связный список


    // Добавляем таску в конец истории, проверяя, что массив ещё не заполнен, если заполнен,
    // то заменяем нулевой элемент
    @Override
    public void add(Task task) {

        if (task != null) {
            taskHistory.linkLast(task);
        }
    }


    // Возвращаем список с историей
    @Override
    public List<Task> getHistory() {
        return taskHistory.getTasks();
    }

    // Удаляем из истории просмотров
    @Override
    public void remove(int id) {
        taskHistory.remove(id);
    }

    // Удаляем всю историю
    @Override
    public void clearAll() {
        taskHistory.tail = null;
        taskHistory.head = null;
        taskHistory.taskHistory.clear();
    }


    // Реализуем собственный двусвязный список
    class HistoryLinkedList {
        private final HashMap<Integer, Node> taskHistory = new HashMap<>(); // Храним id и узлы
        private Node head;
        private Node tail;

        // Добавляем задачу в конец списка
        public void linkLast(Task task) {

            if (taskHistory.containsKey(task.getTaskId())) { // Если таска есть в истории, то удаляем её
                remove(task.getTaskId());
            }

            Node tmp = new Node(task);

            if (head == null) {
                head = tmp;
            } else {
                tail.next = tmp;
            }
            tmp.prev = tail;
            tail = tmp;
            taskHistory.put(tmp.task.getTaskId(), tmp);
        }

        // Удаляем таску из истории и связного списка
        public void remove(int id) {
            if (taskHistory.containsKey(id)) {
                Node node = taskHistory.get(id);
                Node left = node.prev;
                Node right = node.next;

                // Проверяем, что мы не пытаемся удалить первый элемент, или последний
                if (left != null && right != null) {
                    left.next = right;
                    right.prev = left;
                } else if (left == null && right != null) { // если элемент в начале списка и он не один
                    right.prev = null;
                    this.head = right;
                } else if (left != null) { // если элемент в конце списка и он не один
                    left.next = null;
                    this.tail = left;
                } else { // если один элемент в списке и его хотим удалить
                    this.head = null;
                    this.tail = null;
                }

                taskHistory.remove(id);
            }
        }

        // Собираем задачи в List от хвоста к голове, т.е. начиная с последней просмотренной задачи
        public List<Task> getTasks() {
            ArrayList<Task> list = new ArrayList<>();
            Node node = tail;
            while (node != null) {
                list.add(node.task);
                node = node.prev;
            }
            return list;

        }
    }
}
