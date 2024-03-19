package service;

import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TaskManager {
    // Класс для управления задачами
    private static int id; // Переменная для хранения id задачи
    private final HashMap<Integer, Task> tasks; // хранилище всех задач

    public TaskManager() {
        id = 1;
        tasks = new HashMap<>();
    }

    public static int getTaskId() {
        return id;
    }

    public static void setTaskId() {
        id++;
    }


    // Получаем список всех задач
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>(List.copyOf(tasks.values()));
        return allTasks;

    }

    // Проверяем есть ли задача в менеджере
    public boolean hasTask(int id) {
        return tasks.containsKey(id);
    }

    // Удаляем все задачи
    public void deleteAllTasks() {
        tasks.clear();
    }

    // Удаляем все эпики
    public void delAllEpics() {
        // Наверное проще было сделать в TaskManager три мапы под каждый класс Task, Epic, Subtask ?
        for (Integer key: getAllEpics().keySet()) {
            delAllSubtasks(tasks.get(key)); // удаляем подзадачи
            tasks.remove(key);
        }
    }

    // Добавляем задачу в менеджер
    public void addTask(Task task) {
        tasks.put(task.getTaskId(), task);
        // Если прилетает подзадача, то добавляем её в мапу эпика
        if (task.getClass() == Subtask.class) {
            Epic epic = ((Subtask) task).getEpic();
            epic.addSubtask((Subtask) task);
        }
    }

    // Обновляем задачу
    public void updateTask(int id, String name, String description) {
        Task task = tasks.get(id);
        task.setName(name);
        task.setDescription(description);
    }

    // Меняем статус задачи
    public void updateTaskStatus(int id, TaskStatus taskStatus) {
        Task task = tasks.get(id);
        if (task.getClass() != Epic.class) {
            task.setTaskStatus(taskStatus);
        }
    }

    // Удаляем задачу по id
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
    public void delAllSubtasks(Task task) {
        for (Integer key: task.getSubtasks().keySet()) {
            tasks.remove(key);
        }
    }

    // Получаем все подзадачи
    public HashMap<Integer, Subtask> getAllSubtasks() {
        HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
        HashMap<Integer, Epic> allEpics = getAllEpics();
        for (Integer key: allEpics.keySet()) {
            allSubtasks.putAll(allEpics.get(key).getSubtasks());
        }
        return allSubtasks;
    }

    // Получаем задачу по id
    public Task getTaskById(int id) {
        for (Integer key : tasks.keySet()) {
            if (tasks.get(key).getTaskId() == id) {
                return tasks.get(key);
            }
        }
        return null;
    }

    // Получаем все эпики
    public HashMap<Integer, Epic> getAllEpics() {
        HashMap<Integer, Epic> allEpics = new HashMap<>();
        for (Integer key: tasks.keySet()) {
            if (tasks.get(key).getClass() == Epic.class) {
                Epic epic = (Epic) tasks.get(key);
                allEpics.put(epic.getTaskId(), epic);
            }
        }
        return allEpics;
    }

    // Получаем подзадачу по id эпика и id подзадачи
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
        return epic.getSubtaskById(subtaskId);
    }

    // Получаем все подзадачи эпика по id
    public ArrayList<Subtask> getAllSubtasksById(int id) {
        // хз какая логика будет на фронте, поэтому пока возвращается null если id выводит не эпик
        ArrayList<Subtask> allSubtasks = new ArrayList<>(List.copyOf(tasks.get(id).getSubtasks().values()));
        return allSubtasks;
    }

    // Меняем статус задачи
    public void setTaskStatus(int id, TaskStatus taskStatus) {
        if (!hasTask(id) || tasks.get(id).getClass() == Epic.class) {
            return;
        }
        tasks.get(id).setTaskStatus(taskStatus);
    }

}
