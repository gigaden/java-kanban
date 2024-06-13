package ru.yandex.todo.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import ru.yandex.todo.exceptions.DurationAdapterException;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null || duration.isZero()) {
            jsonWriter.value((String) null);
        } else {
            jsonWriter.value(String.valueOf(duration.toMinutes()));
        }

    }

    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        var peek = jsonReader.peek();
        if (peek == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }
        try {
            return Duration.ofMinutes(Integer.parseInt(jsonReader.nextString()));
        } catch (NumberFormatException e) {
            throw new DurationAdapterException("Задана неверная продолжительность задачи");
        }
    }
}