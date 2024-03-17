public class CreateTasks {
    public static void createTasks(TaskManager taskManager) {

        // Создаём задачи, эпики и подзадачи
        Task task1 = new Task("Задача 1", "Описание задачи 1");
        taskManager.addTask(task1);
        Task task2 = new Task("Задача 2", "Описание задачи 2");
        taskManager.addTask(task2);
        Task task3 = new Task("Задача 3", "Описание задачи 3");
        taskManager.addTask(task3);
        System.out.println();

    }
}