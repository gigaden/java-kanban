package ru.yandex.todo.exceptions;

public class DurationAdapterException extends RuntimeException {
    // Создаём своё непроверяемое исключение

    public DurationAdapterException() {
        super();
    }

    public DurationAdapterException(String message) {
        super(message);
    }

    public DurationAdapterException(String message, Throwable cause) {
        super(message, cause);
    }
}
