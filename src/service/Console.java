package service;

import model.Epic;
import model.Subtask;
import model.Task;
import service.TaskManager;
import service.TaskStatus;

import java.util.Scanner;
import java.util.HashMap;
public class Console {

    // Выводим главное меню
    public static void printMenu() {
        System.out.println("Введите цифру для отображения:");
        System.out.println("1 - посмотреть список всех задач");
        System.out.println("2 - посмотреть задачу");
        System.out.println("3 - добавить новую задачу");
        System.out.println("4 - добавить новый эпик");
        System.out.println("5 - добавить подзадачу");
        System.out.println("6 - очистить все задачи");
        System.out.println("7 - изменить имя и описание задачи");
        System.out.println("8 - изменить статус задачи");
        System.out.println("9 - удалить задачу");
        System.out.println("10 -  удалить все эпики");
        System.out.println("11 - посмотреть подзадачи");
        System.out.println("12 - посмотреть все Эпики");
        System.out.println("13 - посмотреть все Подзадачи");
        System.out.println("14 - завершить работу");
    }


    // Выводим задачи на печать
    public static void printTasks(HashMap<Integer, Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Не создано ни одной задачи\n");
            return;
        }
        for (Integer key: tasks.keySet()) {
            Task task = tasks.get(key);
            System.out.println(task);
            System.out.println("____________________________________\n");
        }
    }

    // Меняем описание задачи
    public static void updateTask(TaskManager taskManager, Scanner sc) {
        System.out.println("Введите id задачи");
        int id = Integer.parseInt(sc.nextLine());
        if (!taskManager.getAllTasks().containsKey(id)) {
            System.out.println("Задачи с таким id нет\n");
            return;
        }
        Task task = taskManager.getTaskById(id);
        System.out.println("Введите новое имя задачи. Прежнее: " + task.getName());
        String name = sc.nextLine();
        System.out.println("Введите новое описание задачи. Прежнее: " + task.getDescription());
        String description = sc.nextLine();
        taskManager.updateTask(id, name, description);
        System.out.println("Изменения сохранены\n");
    }

    //Меняем статус задачи
    public static void updateStatus(TaskManager taskManager, Scanner sc) {
        System.out.println("Введите id задачи");
        int id = Integer.parseInt(sc.nextLine());
        if (!taskManager.getAllTasks().containsKey(id)) {
            System.out.println("Задачи с таким id нет\n");
            return;
        }
        Task task = taskManager.getTaskById(id);
        System.out.println("Введите новый статус задачи. Прежний: " + task.getTaskStatus());
        System.out.println("Допустимо: new, in_progress, done");
        String status = sc.nextLine().toUpperCase();
        TaskStatus command = TaskStatus.valueOf(status);
        switch(command) {
            case NEW -> taskManager.updateTaskStatus(id, TaskStatus.NEW);
            case IN_PROGRESS -> taskManager.updateTaskStatus(id, TaskStatus.IN_PROGRESS);
            case DONE -> taskManager.updateTaskStatus(id, TaskStatus.DONE);
            default -> System.out.println("Такого статуса не существует\n");
        }
        System.out.println("Изменения сохранены\n");
    }

    // Удаляем задачу
    public static void delTask(TaskManager taskManager, Scanner sc) {
        System.out.println("Введите id задачи");
        int id = Integer.parseInt(sc.nextLine());
        if (!taskManager.getAllTasks().containsKey(id)) {
            System.out.println("Задачи с таким id нет\n");
            return;
        }
        taskManager.delTaskById(id);
        System.out.println("Задача удалена\n");
    }

    // Получаем подзадачи
    public static void getSubtasks(TaskManager taskManager, Scanner sc) {
        System.out.println("Введите id эпика");
        int id = Integer.parseInt(sc.nextLine());
        if (!taskManager.getAllTasks().containsKey(id)) {
            System.out.println("Эпика с таким id нет\n");
            return;
        } else if (taskManager.getAllSubtasksById(id) == null) {
            System.out.println("У этой задачи нет подзадач\n");
            return;
        }
        System.out.println(taskManager.getAllSubtasksById(id));
    }

    // Добавляем подзадачу
    public static void addSubtask(TaskManager taskManager, Scanner sc) {
        System.out.println("Введите id эпика");
        int id = Integer.parseInt(sc.nextLine());
        if (!taskManager.getAllTasks().containsKey(id)) {
            System.out.println("Эпика с таким id нет\n");
            return;
        } else if (taskManager.getTaskById(id).getSubtasks() == null) {
            System.out.println("Это обычная задачу, добавить в неё подзадачу нельзя\n");
            return;
        }
        System.out.println("Введите имя подзадачи.");
        String name = sc.nextLine();
        System.out.println("Введите описание подзадачи.");
        String description = sc.nextLine();
        Epic epic = (Epic) taskManager.getTaskById(id);
        Subtask subtask = new Subtask(epic, name, description);
        taskManager.addTask(subtask);
        System.out.println("Подзадача добавлена\n");
    }

}