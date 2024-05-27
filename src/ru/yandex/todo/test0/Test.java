//package ru.yandex.todo.test0;
//
//import ru.yandex.todo.model.Epic;
//import ru.yandex.todo.model.Subtask;
//import ru.yandex.todo.model.Task;
//import ru.yandex.todo.service.InMemoryTaskManager;
//import ru.yandex.todo.service.TaskStatus;
//
//import java.util.ArrayList;
//
//public class Test {
//    public static void main(String[] args) {
//        System.out.println("Тест запущен");
//        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
//        // CreateTasks.createTasks(InMemoryTaskManager); // наполнить тасками
//        // CreateEpicsSubtasks.createEpics(InMemoryTaskManager); // наполнить эпиками и подзадачами
//        // System.out.println("Наполнили базу");
//
//        // Добавляем задачу и эпик
//        Task task1 = new Task("Test 1", "Test 1 description");
//        inMemoryTaskManager.addTask(task1);
//        System.out.println("Добавили новую задачу:");
//        System.out.println(inMemoryTaskManager.getTaskById(task1.getTaskId()));
//        System.out.println("Добавили новый эпик:");
//        Epic epic1 = new Epic("Test epic 1", "Test epic 1 description");
//        inMemoryTaskManager.addTask(epic1);
//        System.out.println(inMemoryTaskManager.getTaskById(epic1.getTaskId()));
//        System.out.println("______________________________________");
//
//        // Добавляем подзадачу
//        Subtask subtask1 = new Subtask(epic1, "Test subtask 1", "Test subtask 1 description");
//        inMemoryTaskManager.addTask(subtask1);
//        System.out.println("Добавили новую подзадачу в него:");
//        System.out.println(inMemoryTaskManager.getTaskById(subtask1.getTaskId()));
//        System.out.println("______________________________________");
//
//
//        System.out.println("Выводим все задачи");
//        System.out.println(inMemoryTaskManager.getAllTasks());
//        System.out.println("______________________________________");
//
//        System.out.println("Выводим все подзадачи по id эпика");
//        System.out.println(inMemoryTaskManager.getAllSubtasksById(epic1.getTaskId()));
//        System.out.println("______________________________________");
//
//        System.out.println("Выводим эпик, его статус, меняем статус подзадачи, выводим ещё раз эпик");
//        System.out.println(inMemoryTaskManager.getTaskById(epic1.getTaskId()));
//        System.out.println("Пробуем поменять статус эпика, меняться не должен");
//        inMemoryTaskManager.setTaskStatus(epic1.getTaskId(), TaskStatus.DONE);
//        System.out.println(inMemoryTaskManager.getTaskById(epic1.getTaskId()));
//        System.out.println("______________________________________");
//
//        System.out.println("Меняем статус подзадачи на IN_PROGRESS");
//        inMemoryTaskManager.setTaskStatus(subtask1.getTaskId(), TaskStatus.IN_PROGRESS);
//        System.out.println(inMemoryTaskManager.getTaskById(epic1.getTaskId()));
//        System.out.println("______________________________________");
//
//        System.out.println("Меняем статус подзадачи на DONE");
//        inMemoryTaskManager.setTaskStatus(subtask1.getTaskId(), TaskStatus.DONE);
//        System.out.println(inMemoryTaskManager.getTaskById(epic1.getTaskId()));
//        System.out.println("______________________________________");
//
//        System.out.println("Удаляем подзадачу");
//        inMemoryTaskManager.delTaskById(subtask1.getTaskId());
//        System.out.println("Выводим все задачи");
//        System.out.println(inMemoryTaskManager.getAllTasks());
//        System.out.println("____________________________________");
//        System.out.println();
//
//        System.out.println("Получаем список всех эпиков");
//        System.out.println("счётчик в таскменеджер:" + inMemoryTaskManager.getTaskId());
//        ArrayList<Epic> allEpics = inMemoryTaskManager.getAllEpics();
//        System.out.println(allEpics);
//        System.out.println("Пробуем поменять имя одного из эпиков в полученных данных");
//        allEpics.get(0).setName("Успешно изменили имя эпика");
//        System.out.println(inMemoryTaskManager.getAllEpics());
//        System.out.println("счётчик в таскменеджер:" + InMemoryTaskManager.getTaskId());
//        System.out.println("____________________________________");
//        System.out.println();
//
//        Subtask subtask = new Subtask(epic1, "Имя подзадачи прежнее", "Описание");
//        inMemoryTaskManager.addTask(subtask);
//        System.out.println("Получаем список всех подзадач");
//        ArrayList<Subtask> allSubtask = inMemoryTaskManager.getAllSubtasks();
//        System.out.println(allSubtask);
//        System.out.println("Пробуем поменять имя одной из подзадач в полученных данных");
//        allSubtask.get(0).setName("Успешно изменили имя подзадачи");
//        System.out.println(inMemoryTaskManager.getAllSubtasks());
//        System.out.println("счётчик в таскменеджер:" + inMemoryTaskManager.getTaskId());
//
//
//    }
//}
