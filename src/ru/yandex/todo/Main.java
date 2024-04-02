package ru.yandex.todo;

import ru.yandex.todo.filltasks.CreateEpicsSubtasks;
import ru.yandex.todo.filltasks.CreateTasks;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Task;
import ru.yandex.todo.service.*;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        /* Логика работы следующая: - ru.yandex.todo.service.InMemoryTaskManager - для управления задачами и хранении задач в хэшмапе.
        В хэшмапу tasks попадают все задачи, подзадачи и эпики с уникальным id.
        * - ru.yandex.todo.model.Task - базовый класс для задач, эпиков и подзадач. Т.к. эпики должны знать о своих подзадачах,
        * а подзадачи об эпике, к которому принадлежат, я добавил поле subtasks, которое в объектах ru.yandex.todo.model.Epic будет хранить
        * подзадачи, а в объектах других классах там будет лежать null.
        - ru.yandex.todo.model.Epic - хранит в себе мапу подзадач. Возможно было бы проще не делать эту мапу, а в случае необходимости
        найти подзадачи эпика простов фильтровать их по id. Метод checkSubtasksStatusDone проверяет при каждой смене
        статуса подзадачи всю мапу подзадач эпика и, при необходимости, меняет статус самого эпика.
        - ru.yandex.todo.model.Subtask - описывает подзадачу эпика. Добавил туда поле с объектом эпика,  чтобы субтаск знал к какой
        подзадаче он относится и мог обратиться при смене своего статуса к методу эпика checkSubtasksStatusDone()
        - ru.yandex.todo.Console - делал для себя, чтобы было более удобно просматривать как и что работает.
        - ru.yandex.todo.filltasks.CreateTasks и ru.yandex.todo.filltasks.CreateEpicsSubtasks - чтоб данными ru.yandex.todo.service.InMemoryTaskManager наполнить. */
        // Спасибо за ревью, всё по делу! Возможно при проектировании правильнее было в InMemoryTaskManager создать
        // три хэшмапа для Task, Epic и Subtask отдельно?


        TaskManager inMemoryTaskManager = Managers.getDefault();
        CreateTasks.createTasks(inMemoryTaskManager); // наполнить тасками
        CreateEpicsSubtasks.createEpics(inMemoryTaskManager); // наполнить эпиками и подзадачами
        Scanner sc = new Scanner(System.in);
        Console console = new Console();

        while (true) {
            console.printMenu();
            int command = Integer.parseInt(sc.nextLine());
            switch (command) {
                case 1: {
                    console.printTasks(inMemoryTaskManager.getAllTasks());
                    break;
                }
                case 2: {
                    System.out.println("Введите id задачи");
                    int id = Integer.parseInt(sc.nextLine());
                    if (inMemoryTaskManager.hasTask(id)) {
                        System.out.println(inMemoryTaskManager.getTaskById(id));
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
                    inMemoryTaskManager.addTask(new Task(name, description));
                    System.out.println("Задача добавлена");
                    break;
                }
                case 4: {
                    System.out.println("Введите название эпика");
                    String name = sc.nextLine();
                    System.out.println("Введите описание эпика");
                    String description = sc.nextLine();
                    inMemoryTaskManager.addTask(new Epic(name, description));
                    System.out.println("Эпик добавлен\n");
                    break;
                }
                case 5: {
                    console.addSubtask(inMemoryTaskManager, sc);
                    break;
                }
                case 6: {
                    inMemoryTaskManager.deleteAllTasks();
                    break;
                }
                case 7: {
                    console.updateTask(inMemoryTaskManager, sc);
                    break;
                }
                case 8: {
                    console.updateStatus(inMemoryTaskManager, sc);
                    break;
                }
                case 9: {
                    console.delTask(inMemoryTaskManager, sc);
                    break;
                }
                case 10: {
                    inMemoryTaskManager.delAllEpics();
                    System.out.println("Все эпики удалены\n");
                    break;
                }
                case 11: {
                    console.getSubtasks(inMemoryTaskManager, sc);
                    break;
                }
                case 12: {
                    System.out.println(inMemoryTaskManager.getAllEpics());
                    System.out.println();
                    break;
                }
                case 13: {
                    System.out.println(inMemoryTaskManager.getAllSubtasks());
                    System.out.println();
                    break;
                }
                case 14: {
                    System.out.println(inMemoryTaskManager.getHistory());
                    break;
                }
                case 15: {
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
