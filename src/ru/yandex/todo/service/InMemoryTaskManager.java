package ru.yandex.todo.service;

import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    // Класс для управления задачами
    private static int id; // Переменная для хранения id задачи
    private final HashMap<Integer, Task> tasks; // Хранилище всех задач

    protected final HistoryManager historyManager; // Менеджер для управления историей просмотров задач

    public InMemoryTaskManager() {
        id = 1;
        tasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    public static int getTaskId() {
        return id;
    }

    public static void setTaskId() {
        id++;
    }


    // Получаем список всех задач
    /* Если я возвращаю через copyOf, то я копирую только массив, объекты которые в нём лежат
     * ссылки же и, меняя их, они меняются в главной мапе, тест тогда один не проходит.
     * Может я не понял чего просто? */
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(List.copyOf(tasks.values()));
    }

    // Получаем все эпики
    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getClass() == Epic.class) {
                allEpics.add((Epic) task);
            }
        }
        return allEpics;
    }

    // Получаем все подзадачи
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Epic epic : getAllEpics()) {
            allSubtasks.addAll(epic.getSubtasks().values());
        }
        return allSubtasks;
    }

    // Проверяем есть ли задача в менеджере
    public boolean hasTask(int id) {
        return tasks.containsKey(id);
    }

    // Удаляем все задачи
    @Override
    public void deleteAllTasks() {
        tasks.clear();
        historyManager.clearAll();
    }

    // Удаляем все эпики
    @Override
    public void delAllEpics() {
        // Наверное проще было сделать в InMemoryTaskManager три мапы под каждый класс Task, Epic, Subtask ?
        for (Epic epic : getAllEpics()) {
            delAllSubtasks(epic); // удаляем подзадачи
            tasks.remove(epic.getTaskId());
        }
    }

    // Добавляем задачу в менеджер
    @Override
    public int addTask(Task task) {
        // Если прилетает подзадача, то добавляем её в мапу эпика, проверив существует ли в мапе сам эпик
        if (task.getClass() == Subtask.class) {
            Epic epic = ((Subtask) task).getEpic();
            if (hasTask(epic.getTaskId())) {
                epic.addSubtask((Subtask) task);
                tasks.put(task.getTaskId(), task);
                return task.getTaskId();
            }
        }
        tasks.put(task.getTaskId(), task);
        return task.getTaskId();
    }

    // Обновляем задачу
    @Override
    public void updateTask(int id, String name, String description) {
        Task task = tasks.get(id);
        task.setName(name);
        task.setDescription(description);
    }

    // Меняем статус задачи
    @Override
    public void updateTaskStatus(int id, TaskStatus taskStatus) {
        Task task = tasks.get(id);
        if (task.getClass() != Epic.class) {
            task.setTaskStatus(taskStatus);
        }
    }

    // Удаляем задачу по id
    @Override
    public void delTaskById(int id) {
        /* Т.к. решил хранить подзадачи в мапе здесь и в мапе эпика, то нужно дополнительно удалять
         * подзадачу из мапы эпика */
        if (tasks.get(id).getClass() == Subtask.class) {
            Subtask subtask = (Subtask) tasks.get(id);
            Epic epic = subtask.getEpic();
            epic.delSubtaskById(id); // удаляем из эпика
            tasks.remove(id);
            historyManager.remove(id);
            epic.checkSubtasksStatusDone(); // проверяем статусы эпика
        } else if (tasks.get(id).getClass() == Epic.class) {
            delAllSubtasks(tasks.get(id)); // Если задача - эпик, то очищаем его хэшмапу с субтасками
        }
        historyManager.remove(id);
        tasks.remove(id);

    }

    // Удаляем все подзадачи эпика
    @Override
    public void delAllSubtasks(Task task) {
        for (Integer key : task.getSubtasks().keySet()) {
            tasks.remove(key);
            historyManager.remove(key);
        }
    }

    // Получаем задачу по id
    @Override
    public Task getTaskById(int id) {
        for (Integer key : tasks.keySet()) {
            if (tasks.get(key).getTaskId() == id) {
                addTaskToHistory(tasks.get(key));
                return tasks.get(key);
            }
        }
        return null;
    }

    // Получаем подзадачу по id эпика и id подзадачи
    @Override
    public Subtask getSubtaskById(int taskId, int subtaskId) {
        if (!hasTask(taskId) || tasks.get(taskId).getSubtasks() == null) {
            return null;
        }
        Epic epic = (Epic) tasks.get(taskId);
        Subtask subtask = epic.getSubtaskById(subtaskId);
        if (subtask != null) {
            addTaskToHistory(subtask);
        }
        return subtask;
    }

    // Получаем все подзадачи эпика по id
    @Override
    public ArrayList<Subtask> getAllSubtasksById(int id) {
        // хз какая логика будет на фронте, поэтому пока возвращается null если id выводит не эпик
        ArrayList<Subtask> allSubtasks = new ArrayList<>(List.copyOf(tasks.get(id).getSubtasks().values()));
        return allSubtasks;
    }

    // Меняем статус задачи
    @Override
    public void setTaskStatus(int id, TaskStatus taskStatus) {
        if (!hasTask(id) || tasks.get(id).getClass() == Epic.class) {
            return;
        }
        tasks.get(id).setTaskStatus(taskStatus);
    }

    // Получаем историю из 10 последних просмотренных задач
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void addTaskToHistory(Task task) {
        historyManager.add(task);
    }

}
