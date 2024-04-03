package ru.yandex.todo.service;

// Этот класс типо паттерн фабрика, или я не правильно понял как его сделать и для чего он?
public final class Managers {

    private Managers() {} // Запрещаем создавать экземпляры класса
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
