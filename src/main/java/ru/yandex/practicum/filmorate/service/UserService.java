package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
        log.info("Добавление нового друга c id {} для пользователя с id {}", friendId, userId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUser(userId);
        User friend = userStorage.getUser(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
        log.info("Удаление друга c id {} для пользователя с id {}", friendId, userId);
    }

    public List<User> getUserFriendList(int userId) {
        User user = userStorage.getUser(userId);
        List<User> userList = userStorage.getUserList();
        Set<Integer> userFriendIdList = user.getFriends();
        return getFilteredUserListByFilteredIdList(userFriendIdList, userList);
    }

    public List<User> getUsersCommonFriend(int userId, int otherUserId) {
        User user = userStorage.getUser(userId);
        User otherUser = userStorage.getUser(otherUserId);
        List<User> userList = userStorage.getUserList();
        Set<Integer> commonFriendsIdList = new HashSet<>(user.getFriends());
        commonFriendsIdList.retainAll(otherUser.getFriends());
        return getFilteredUserListByFilteredIdList(commonFriendsIdList, userList);
    }

    private List<User> getFilteredUserListByFilteredIdList(Set<Integer> filteredUserIdList, List<User> userList) {
        ArrayList<User> filteredUserList = new ArrayList<>(filteredUserIdList.size());
        if (!filteredUserIdList.isEmpty()) {
            for (Integer id : filteredUserIdList) {
                for (User user : userList) {
                    if (user.getId() == id) {
                        filteredUserList.add(userStorage.getUser(id));
                    }
                }
            }
        }
        return filteredUserList;
    }
}
