package ru.yandex.todo.exceptions;

public class ManagerCrossTimeException extends RuntimeException {
    // Создаём своё непроверяемое исключение

    public ManagerCrossTimeException() {
        super();
    }

    public ManagerCrossTimeException(String message) {
        super(message);
    }

    public ManagerCrossTimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
