package ru.yandex.todo.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.todo.adapter.DurationAdapter;
import ru.yandex.todo.adapter.LocalDateAdapter;

import java.time.Duration;
import java.time.LocalDateTime;

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
