package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User getUser(int id);

    List<User> getUserList();

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

    void deleteUserList();

    void addConfFriendship(int userId, int friendId);

    void addUnconfFriendship(int userId, int friendId);

    void removeFriendship(int userId, int friendId);

    List<User> getUserFriends(int userId);
}
