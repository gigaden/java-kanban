package ru.yandex.todo.service;

import com.google.gson.GsonBuilder;

import java.time.Duration;
import java.time.LocalDateTime;
import ru.yandex.todo.adapter.*;

import com.google.gson.Gson;
import ru.yandex.todo.model.Epic;

// Этот класс типо паттерн фабрика, или я не правильно понял как его сделать и для чего он?
public final class Managers {

    private Managers() {  // Запрещаем создавать экземпляры класса

    }

    public static InMemoryTaskManager getDefault() {

        return new InMemoryTaskManager();
    }

    public static InMemoryHistoryManager getDefaultHistory() {

        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        return new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}
