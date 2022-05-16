package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class Film {
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int duration;
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;

    public Film(String name, String description, String releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate, FORMATTER);
        this.duration = duration;
    }
}
