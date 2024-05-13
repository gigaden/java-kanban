package ru.yandex.todo.service;

import ru.yandex.todo.exceptions.ManagerSaveException;
import ru.yandex.todo.model.Epic;
import ru.yandex.todo.model.Subtask;
import ru.yandex.todo.model.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    // Класс с функцие сохранения задач в файл и их восстановления

    private File database;


    public static void main(String[] args) {
        final String FILE_NAME = "db.csv";
        File file = new File(FILE_NAME);

        // Накидываем тасков
        FileBackedTaskManager managerOutput = new FileBackedTaskManager(file);
        Task task1 = new Task("task1", "description1");
        managerOutput.addTask(task1);
        Epic epic1 = new Epic("epic1", "desc1");
        managerOutput.addTask(epic1);
        Subtask subtask1 = new Subtask(epic1, "subtask1", "descr");
        managerOutput.addTask(subtask1);
        System.out.println("Сохранённые таски первым менеджером:");
        System.out.println(managerOutput.getAllTasks());
        System.out.println("_____________________________");

        // Читаем из файла
        FileBackedTaskManager managerInput = FileBackedTaskManager.loadFromFile(file);
        Task task = new Task("newTask", "descr");
        managerInput.addTask(task);
        System.out.println("Таски, загруженные во второй менеджер + 1 новая:");
        System.out.println(managerInput.getAllTasks());
        System.out.println("_____________________________");

    }

    public FileBackedTaskManager(File file) {
        if (file == null) {
            throw new ManagerSaveException("Передан null вместо файла");
        }
        database = file;
    }

    public File getDatabase() {
        return database;
    }


    // Восстанавливаем данные из файла при запуске приложения
    public static FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException {
        if (file == null) {
            throw new ManagerSaveException("Передан null вместо файла");
        }
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int idCount = 1; // Инициализируем счётчик для новых задач
        try {
            List<String> databaseList = Files.readAllLines(file.toPath());
            for (int i = 1; i < databaseList.size(); i++) {
                Task task = manager.fromString(databaseList.get(i));
                if (idCount < task.getTaskId()) {
                    idCount = task.getTaskId();
                }
                manager.loadTaskToMap(task);
            }
            // Меняем счётчик для новых задач, делая его на 1 больше максимального в файле
            FileBackedTaskManager.changeId(idCount + 1);

            return manager;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ManagerSaveException();
        }
    }


    //  Сохраняем текущее состоянее менеджера в файл
    public void save() throws ManagerSaveException {
        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter(String.valueOf(database), StandardCharsets.UTF_8))) {
            writer.write("id,type,name,status,description,epic\n"); // Пишем хэдер
            for (Task task : getAllTasks()) {
                writer.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException();
        }
    }


    // Создаём задачу из строки
    public Task fromString(String value) {
        String[] taskList = value.split(", ");
        switch (TaskType.valueOf(taskList[1])) {
            case TASK -> {
                Task task = new Task();
                task.setName(taskList[2]);
                task.setDescription(taskList[4]);
                task.setTaskStatus(TaskStatus.valueOf(taskList[3]));
                task.setTaskId(Integer.parseInt(taskList[0]));
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic();
                epic.setName(taskList[2]);
                epic.setDescription(taskList[4]);
                epic.setTaskStatus(TaskStatus.valueOf(taskList[3]));
                epic.setTaskId(Integer.parseInt(taskList[0]));

                // Проверяем есть ли сабтаски эпика в мапе, если есть, то закидываем их к эпику
                ArrayList<Task> tasks = getAllTasks();
                for (Task task : tasks) {
                    if (task.getClass() == Subtask.class) {
                        Subtask subtask = (Subtask) task;
                        if (subtask.getEpic().equals(epic)) {
                            epic.addSubtask(subtask);
                        }
                    }
                }
                return epic;
            }
            case SUBTASK -> {
                // Проверяем есть ли эпик сабтаска в мапе
                /* Вопрос самому себе на подумать и доработать в каникулы:
                Здесь нужно решить: мапа не упорядочена, если сабтаска прилетит раньше эпика из файла,
                * то возникнет проблема, т.к. такого эпика в мапе не будет. Если создавать новый объект эпика
                * и прокидывать его в сабтаску, то при сеттере таск статуса возникнет ошибка проверки статусов.
                * Как вариант решения проблемы: заменить поля связей эпиков и сабтасок на их id, а не объекты,
                * переделать метод epic.checkSubtasksStatusDone() в методе Subtask.setTaskStatus.
                * */
                Epic epic = (Epic) getTaskById(Integer.parseInt(taskList[5]));
                Subtask subtask = new Subtask(epic);
                subtask.setName(taskList[2]);
                subtask.setDescription(taskList[4]);
                subtask.setTaskStatus(TaskStatus.valueOf(taskList[3]));
                subtask.setTaskId(Integer.parseInt(taskList[0]));
                epic.addSubtask(subtask);
                return subtask;
            }
        }
        return null;
    }


    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }


    @Override
    public void setTaskStatus(int id, TaskStatus taskStatus) {
        super.setTaskStatus(id, taskStatus);
        save();
    }

    @Override
    public void delAllSubtasks(Task task) {
        super.delAllSubtasks(task);
        save();
    }

    @Override
    public void delTaskById(int id) {
        super.delTaskById(id);
        save();
    }

    @Override
    public void updateTaskStatus(int id, TaskStatus taskStatus) {
        super.updateTaskStatus(id, taskStatus);
        save();
    }

    @Override
    public void updateTask(int id, String name, String description) {
        super.updateTask(id, name, description);
        save();
    }

    @Override
    public void delAllEpics() {
        super.delAllEpics();
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }
}
