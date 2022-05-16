package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class User {
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public User(String email, String login, String name, String birthday) {
        this.email = email;
        this.login = login;
        if (name.isBlank() || name.isEmpty()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = LocalDate.parse(birthday, FORMATTER);
    }
}
