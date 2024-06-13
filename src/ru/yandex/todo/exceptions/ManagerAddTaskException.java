package ru.yandex.todo.exceptions;

public class ManagerAddTaskException extends RuntimeException {
    // Создаём своё непроверяемое исключение

    public ManagerAddTaskException() {
        super();
    }

    public ManagerAddTaskException(String message) {
        super(message);
    }

    public ManagerAddTaskException(String message, Throwable cause) {
        super(message, cause);
    }
}
