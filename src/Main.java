import model.Epic;
import model.Task;
import service.Console;
import service.CreateEpicsSubtasks;
import service.CreateTasks;
import service.TaskManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        /* Логика работы следующая: - service.TaskManager - для управления задачами и хранении задач в хэшмапе.
        В хэшмапу tasks попадают все задачи, подзадачи и эпики с уникальным id.
        * - model.Task - базовый класс для задач, эпиков и подзадач. Т.к. эпики должны знать о своих подзадачах,
        * а подзадачи об эпике, к которому принадлежат, я добавил поле subtasks, которое в объектах model.Epic будет хранить
        * подзадачи, а в объектах других классах там будет лежать null.
        - model.Epic - хранит в себе мапу подзадач. Возможно было бы проще не делать эту мапу, а в случае необходимости
        найти подзадачи эпика простов фильтровать их по id. Метод checkSubtasksStatusDone проверяет при каждой смене
        статуса подзадачи всю мапу подзадач эпика и, при необходимости, меняет статус самого эпика.
        - model.Subtask - описывает подзадачу эпика. Добавил туда поле с объектом эпика,  чтобы субтаск знал к какой
        подзадаче он относится и мог обратиться при смене своего статуса к методу эпика checkSubtasksStatusDone()
        - service.Console - делал для себя, чтобы было более удобно просматривать как и что работает.
        - service.CreateTasks и service.CreateEpicsSubtasks - чтоб данными service.TaskManager наполнить. */
        // Спасибо за ревью, всё по делу! Возможно при проектировании правильнее было в TaskManager создать
        // три хэшмапа для Task, Epic и Subtask отдельно?


        TaskManager taskManager = new TaskManager();
        CreateTasks.createTasks(taskManager); // наполнить тасками
        CreateEpicsSubtasks.createEpics(taskManager); // наполнить эпиками и подзадачами
        Scanner sc = new Scanner(System.in);
        Console console = new Console();

        while (true) {
            console.printMenu();
            int command = Integer.parseInt(sc.nextLine());
            switch (command) {
                case 1 : {
                    console.printTasks(taskManager.getAllTasks());
                    break;
                }
                case 2 : {
                    System.out.println("Введите id задачи");
                    int id = Integer.parseInt(sc.nextLine());
                    if (taskManager.hasTask(id)) {
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
                    console.addSubtask(taskManager, sc);
                    break;
                }
                case 6: {
                    taskManager.deleteAllTasks();
                    break;
                }
                case 7: {
                    console.updateTask(taskManager, sc);
                    break;
                }
                case 8: {
                    console.updateStatus(taskManager, sc);
                    break;
                }
                case 9: {
                    console.delTask(taskManager, sc);
                    break;
                }
                case 10: {
                    taskManager.delAllEpics();
                    System.out.println("Все эпики удалены\n");
                    break;
                }
                case 11: {
                    console.getSubtasks(taskManager, sc);
                    break;
                }
                case 12: {
                    System.out.println(taskManager.getAllEpics());
                    System.out.println();
                    break;
                }
                case 13: {
                    System.out.println(taskManager.getAllSubtasks());
                    System.out.println();
                    break;
                }
                case 14: {
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
}
