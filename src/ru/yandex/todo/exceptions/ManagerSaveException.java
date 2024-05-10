package ru.yandex.todo.exceptions;

public class ManagerSaveException extends RuntimeException {
    // Создаём своё непроверяемое исключение

    public ManagerSaveException() {
        super();
    }

    public ManagerSaveException(String message) {
        super(message);
    }
}
