package ru.yandex.todo.exceptions;

public class ManagerAddException extends RuntimeException {
    // Создаём своё непроверяемое исключение

    public ManagerAddException() {
        super();
    }

    public ManagerAddException(String message) {
        super(message);
    }

    public ManagerAddException(String message, Throwable cause) {
        super(message, cause);
    }
}
