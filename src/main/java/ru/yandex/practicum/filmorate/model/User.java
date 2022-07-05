package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private final static String DATE_PATTERN = "yyyy-MM-dd";
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private int id;
    private Set<Integer> friends;

    @Email(message = "Электронная почта не может быть пустой и должна содержать символ @")
    private String email;

    @NotBlank(message = "Логин не может состоять только из пробелов")
    @NotEmpty(message = "Логин не может быть пустым")
    private String login;

    private String name;

    @DateTimeFormat(pattern = DATE_PATTERN)
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    @JsonCreator
    public User(String email, String login, String name, String birthday) {
        this.email = email;
        this.login = login;
        if (name.isBlank() || name.isEmpty()) {
            this.name = login;
        } else {
            this.name = name;
        }
        this.birthday = LocalDate.parse(birthday, FORMATTER);
        this.friends = new HashSet<>();
    }

    public User(int id, String email, String login, String name, LocalDate birthday, HashSet<Integer> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
    }

    public void addFriend(int friendId) {
        friends.add(friendId);
    }

    public void removeFriend(int friendId) {
        friends.removeIf(id -> (id == friendId));
    }
}
