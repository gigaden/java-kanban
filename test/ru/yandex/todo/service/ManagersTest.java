package ru.yandex.todo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {

    @Test
    public void ShouldBePositiveWhenObjectsIsCreated() {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(inMemoryTaskManager, "InMemoryTaskManager не создан");
        Assertions.assertNotNull(inMemoryHistoryManager, "InMemoryHistoryManager не создан");
    }
}
