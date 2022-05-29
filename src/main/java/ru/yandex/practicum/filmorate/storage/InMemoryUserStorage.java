package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final HashMap<Integer, User> userList;
    private int uniqueId;

    public InMemoryUserStorage() {
        userList = new HashMap<>();
    }

    @Override
    public User getUser(int id) {
        if (userList.containsKey(id)) {
            return userList.get(id);
        }
        throw new ResourceNotFoundException(getMessageForUserNotFoundException(id));
    }

    @Override
    public List<User> getUserList() {
        return new ArrayList<>(userList.values());
    }

    @Override
    public User addUser(User newUser) {
        if (newUser.getId() == 0 && newUser.getFriends().isEmpty()) {
            uniqueId += 1;
            newUser.setId(uniqueId);
            userList.put(newUser.getId(), newUser);
            log.info("Добавление нового пользователя c id {}", newUser.getId());
        } else {
            log.warn("Добавление пользователя с некорректными параметрами - {}", newUser);
            throw new ValidationException("Ошибка добавления пользователя. " +
                    "Список друзей пользователя должен быть пуст, а id = 0");
        }
        return newUser;
    }

    @Override
    public User updateUser(User user) {
        int userId = user.getId();
        if (userList.containsKey(userId)) {
            User savedUser = userList.get(userId);
            if (!savedUser.getFriends().equals(user.getFriends())) {
                log.warn("Ошибка обновления информации о пользователе. " +
                        "Список друзей пользователя был некорректно изменен");
                throw new ValidationException("Список друзей пользователя был некорректно изменен");
            }
            userList.put(userId, user);
            log.info("Изменение информации о пользователе с id {}", user.getId());
        } else {
            log.warn("Попытка обновления несуществующего пользователя - {}", user);
            throw new ResourceNotFoundException(getMessageForUserNotFoundException(userId));
        }
        return user;
    }

    @Override
    public void deleteUser(int id) {
        if (userList.containsKey(id)) {
            userList.remove(id);
            log.info("Удаление пользователя с id {}", id);
        } else {
            log.warn("Попытка удаления несуществующего пользователя с id - {}", id);
            throw new ResourceNotFoundException(getMessageForUserNotFoundException(id));
        }
    }

    @Override
    public void deleteUserList() {
        userList.clear();
        log.info("Удаление списка пользователей");
    }

    private String getMessageForUserNotFoundException(int userId) {
        return "Пользователя с id = " + userId + " не существует";
    }
}
