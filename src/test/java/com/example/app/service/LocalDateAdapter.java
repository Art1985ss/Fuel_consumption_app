package com.example.app.service;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateAdapter extends TypeAdapter<LocalDate> {

    @Override
    public void write(JsonWriter jsonWriter, LocalDate localDate) throws IOException {
        jsonWriter.value(localDate.format(DateTimeFormatter.ofPattern("MM.dd.yyyy")));
    }

    @Override
    public LocalDate read(JsonReader jsonReader) throws IOException {
        return LocalDate.parse(jsonReader.nextString());
    }
}
