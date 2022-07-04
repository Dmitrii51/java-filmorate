package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        return userStorage.getUser(userId);
    }

    @GetMapping
    public List<User> getUserList() {
        return userStorage.getUserList();
    }

    @GetMapping("{id}/friends")
    public List<User> getUserFriendList(@PathVariable int id) {
        return userService.getUserFriendList(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonUsersFriendList(@PathVariable int id, @PathVariable int otherId) {
        return userService.getUsersCommonFriend(id, otherId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User newUser) {
        return userStorage.addUser(newUser);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
    }
}
