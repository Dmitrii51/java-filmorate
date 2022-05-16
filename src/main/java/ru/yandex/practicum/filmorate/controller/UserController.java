package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final HashMap<Integer, User> userList = new HashMap<>();
    private int uniqueId;

    @GetMapping
    public List<User> getUserList() {
        return new ArrayList<>(userList.values());
    }

    @PostMapping
    public User createUser(@RequestBody User newUser) {
        if (newUser.getId() == 0 && validateUser(newUser)) {
            uniqueId += 1;
            newUser.setId(uniqueId);
            userList.put(uniqueId, newUser);
            log.info("Добавление нового пользователя c id {}", newUser.getId());
        }
        return newUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        int userId = user.getId();
        if (userList.containsKey(userId) && validateUser(user)) {
            userList.put(userId, user);
            log.info("Изменение информации о пользователе с id {}", user.getId());
        } else {
            log.warn("Ошибка валидации пользователя {} при попытке обновления", user);
            throw new ValidationException("Указанного пользователя не существует");
        }
        return user;
    }

    private boolean validateUser(User user) {
        if (user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации email пользователя - {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        } else if (user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            log.warn("Ошибка валидации логина пользователя - {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации даты рождения пользователя - {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        } else {
            return true;
        }
    }
}
