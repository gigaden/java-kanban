package ru.yandex.todo.service;

import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    // Класс для управления задачами
    private static int id; // Переменная для хранения id задачи
    private final HashMap<Integer, Task> tasks; // Хранилище всех задач

    protected final HistoryManager historyManager;

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
    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            allTasks.add(new Task(task)); // Создаём глубокую копию объекта через конструктор
        }
        return allTasks;
    }

    // Получаем все эпики
    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getClass() == Epic.class) {
                allEpics.add(new Epic((Epic) task));
            }
        }
        return allEpics;
    }

    // Получаем все подзадачи
    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        for (Epic epic : getAllEpics()) {
            Epic copyEpic = new Epic(epic);
            allSubtasks.addAll(copyEpic.getSubtasks().values());
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
        tasks.put(task.getTaskId(), task);
        // Если прилетает подзадача, то добавляем её в мапу эпика
        if (task.getClass() == Subtask.class) {
            Epic epic = ((Subtask) task).getEpic();
            epic.addSubtask((Subtask) task);
        }
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
            epic.checkSubtasksStatusDone(); // проверяем статусы эпика
        } else if (tasks.get(id).getClass() == Epic.class) {
            delAllSubtasks(tasks.get(id)); // Если задача - эпик, то очищаем его хэшмапу с субтасками
        }
        tasks.remove(id);

    }

    // Удаляем все подзадачи эпика
    @Override
    public void delAllSubtasks(Task task) {
        for (Integer key : task.getSubtasks().keySet()) {
            tasks.remove(key);
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
        if (!tasks.containsKey(taskId)) {
            System.out.println("Задачи с таким id не существует.");
            return null;
        }
        if (tasks.get(taskId).getSubtasks() == null) {
            System.out.println("Это обычная задача, у неё нет подзадач.");
            return null;
        }
        Epic epic = (Epic) tasks.get(taskId);
        addTaskToHistory(epic.getSubtaskById(subtaskId));
        return epic.getSubtaskById(subtaskId);
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
    public ArrayList<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void addTaskToHistory(Task task) {
        historyManager.add(task);
    }

}
