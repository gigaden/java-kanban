package ru.yandex.todo.service;

import ru.yandex.todo.exceptions.ManagerCrossTimeException;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    // Класс для управления задачами
    private static int id; // Переменная для хранения id задачи
    private final HashMap<Integer, Task> tasks; // Хранилище всех задач
    private final TreeSet<Task> tasksTreeSet; // Задачи, отсортированные по дате начала

    protected final HistoryManager historyManager; // Менеджер для управления историей просмотров задач

    public InMemoryTaskManager() {
        id = 1;
        tasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        tasksTreeSet = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    public static int getTaskId() {
        return id;
    }

    public static void setTaskId() {
        id++;
    }

    protected static void changeId(int newId) {
        id = newId;
    }


    // Получаем список всех задач
    /* Если я возвращаю через copyOf, то я копирую только массив, объекты которые в нём лежат
     * ссылки же и, меняя их, они меняются в главной мапе, тест тогда один не проходит.
     * Может я не понял чего просто? */
    @Override
    public List<Task> getAllTasks() {
        //return new ArrayList<>(List.copyOf(tasks.values()));
        return List.copyOf(tasks.values());
    }

    // Получаем все эпики
    @Override
    public List<Epic> getAllEpics() {
        return tasks.values().stream()
                .filter(t -> t.getClass() == Epic.class)
                .map(t -> (Epic) t)
                .collect(Collectors.toList());
    }

    // Получаем все подзадачи
    @Override
    public List<Subtask> getAllSubtasks() {
        return getAllEpics().stream()
                .map(e -> e.getSubtasks().values()).flatMap(Collection::stream).collect(Collectors.toList());

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
        tasksTreeSet.clear();
    }

    // Удаляем все эпики
    @Override
    public void delAllEpics() {
        // Наверное проще было сделать в InMemoryTaskManager три мапы под каждый класс Task, Epic, Subtask ?
        for (Epic epic : getAllEpics()) {
            delAllSubtasks(epic); // удаляем подзадачи
            tasksTreeSet.remove(epic);
            tasks.remove(epic.getTaskId());
        }
    }

    // Добавляем задачу в менеджер
    @Override
    public void addTask(Task task) {
        // Проверяем пересекается ли новая задача с какими-либо другими существующими задачами
        if (task.getClass() != Epic.class && task.getStartTime() != null
                && tasksTreeSet.stream().anyMatch(t -> taskIsCrossing(task, t))) {
            throw new ManagerCrossTimeException("Время задачи пересекается с уже созданными задачами.");
        }
        // Если прилетает подзадача, то добавляем её в мапу эпика, проверив существует ли в мапе сам эпик
        if (task.getClass() == Subtask.class) {
            Epic epic = ((Subtask) task).getEpic();
            if (hasTask(epic.getTaskId())) {
                epic.addSubtask((Subtask) task);
                // Пересчитываем и записываем начало, окончание и продолжительность эпика
                epic.setStartTime(epic.getStartTime());
                epic.setEndTime(epic.getEndTime());
                epic.setDuration(epic.getDuration());
            }
        }
        tasks.put(task.getTaskId(), task);
        // Если у задачи не задано время начала, или это эпик, то не записываем её в трисет
        if (task.getClass() != Epic.class && task.getStartTime() != null) {
            tasksTreeSet.add(task);
        }

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
        if (tasksTreeSet.contains(tasks.get(id))) {
            tasksTreeSet.remove(tasks.get(id));
        }
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
            if (tasksTreeSet.contains(tasks.get(key))) {
                tasksTreeSet.remove(tasks.get(key));
            }
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


    // Метод для загрузки таски в мапу из файла
    protected void loadTaskToMap(Task task) {
        tasks.put(task.getTaskId(), task);
        if (task.getClass() != Epic.class && task.getStartTime() != null) {
            tasksTreeSet.add(task);
        }
    }

    // Выводим задачи сортируя по приоритету - по времени начала задачи
    public List<Task> getPrioritizedTasks() {
        return tasksTreeSet.stream().toList();
    }

    // Проверяем пересечение задач по времени
    public boolean taskIsCrossing(Task task1, Task task2) {

        if (tasksTreeSet.contains(task1)) {
            return true;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();
        if (start1.isBefore(start2) && start2.isBefore(end1)) {
            return true;
        } else if (start2.isBefore(start1) && start1.isBefore(end2)) {
            return true;
        }

        return false;
    }

}
