package ru.yandex.practicum.filmorate.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (friend.getFriends().contains(userId)) {
            userStorage.addConfFriendship(userId, friendId);
        } else {
            userStorage.addUnconfFriendship(userId, friendId);
        }
        log.info("Добавление нового друга c id {} для пользователя с id {}", friendId, userId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        if (friend.getFriends().contains(userId)) {
            userStorage.addUnconfFriendship(friendId, userId);
        }
        userStorage.removeFriendship(userId, friendId);
        log.info("Удаление друга c id {} для пользователя с id {}", friendId, userId);
    }

    public List<User> getUserFriendList(int userId) {
        userStorage.getUser(userId);
        return userStorage.getUserFriends(userId);
    }

    public List<User> getUsersCommonFriend(int userId, int otherUserId) {
        userStorage.getUser(userId);
        userStorage.getUser(otherUserId);
        Set<User> commonFriendsIdList = new HashSet<>(userStorage.getUserFriends(userId));
        commonFriendsIdList.retainAll(userStorage.getUserFriends(otherUserId));
        return new ArrayList<>(commonFriendsIdList);
    }
}
