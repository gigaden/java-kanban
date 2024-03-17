import java.util.Scanner;
import java.util.HashMap;
public class Console {
    static int command;
    public static void console(TaskManager taskManager) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            printMenu();
            command = Integer.parseInt(sc.nextLine());
            switch (command) {
                case 1 : {
                    printTasks(taskManager.getAllTasks());
                    break;
                }
                case 2 : {
                    System.out.println("Введите id задачи");
                    int id = Integer.parseInt(sc.nextLine());
                    if (taskManager.getAllTasks().containsKey(id)) {
                        System.out.println(taskManager.getTaskById(id));
                    } else {
                        System.out.println("Задачи с таким id нет\n");
                    }
                    break;
                }
                case 3: {
                    System.out.println("Введите название задачи");
                    String name = sc.nextLine();
                    System.out.println("Введите описание задачи");
                    String description = sc.nextLine();
                    taskManager.addTask(new Task(name, description));
                    System.out.println("Задача добавлена");
                    break;
                }
                case 4: {
                    System.out.println("Введите название эпика");
                    String name = sc.nextLine();
                    System.out.println("Введите описание эпика");
                    String description = sc.nextLine();
                    taskManager.addTask(new Epic(name, description));
                    System.out.println("Эпик добавлен\n");
                    break;
                }
                case 5: {
                    addSubtask(taskManager, sc);
                    break;
                }
                case 6: {
                    taskManager.deleteAllTasks();
                    break;
                }
                case 7: {
                    updateTask(taskManager, sc);
                    break;
                }
                case 8: {
                    updateStatus(taskManager, sc);
                    break;
                }
                case 9: {
                    delTask(taskManager, sc);
                    break;
                }
                case 10: {
                    getSubtasks(taskManager, sc);
                    break;
                }
                case 11: {
                    System.out.println("Работа завершена");
                    return;
                }
                default: {
                    System.out.println("Такой команды нет, попробуйте снова\n");
                    break;
                }
            }
        }
    }

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
        System.out.println("10 - посмотреть подзадачи");
        System.out.println("11 - завершить работу");
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