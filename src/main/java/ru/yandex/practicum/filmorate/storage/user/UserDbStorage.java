package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Component
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final String SQL_GET_USER_BY_ID = "SELECT * FROM users " +
            "WHERE user_id = ?";
    private final String SQL_GET_USER_LIST = "SELECT * FROM users";
    private final String SQL_ADD_NEW_USER = "INSERT INTO users (login, name, birthday, email) VALUES (?, ?, ?, ?)";
    private final String SQL_UPDATE_USER = "UPDATE users SET (login, name, birthday, email) = VALUES (?, ?, ?, ?) " +
            "WHERE user_id = ?";
    private final String SQL_DELETE_USER_BY_ID = "DELETE FROM users " +
            "WHERE user_id = ?";
    private final String SQL_DELETE_USER_LIST = "DELETE FROM users";
    private final String SQL_GET_USER_FRIENDS_ID_LIST = "SELECT friend_id FROM friendship " +
            "WHERE user_id = ?";
    private final String SQL_ADD_FRIEND_FOR_USER = "MERGE INTO friendship (user_id, friend_id, friendship_status_id) " +
            "VALUES (?, ?, ?)";
    private final String SQL_DELETE_USER_FRIEND = "DELETE FROM friendship " +
            "WHERE user_id = ? AND friend_id = ?";
    private final String SQL_GET_USER_FRIENDS_LIST = "SELECT * FROM users " +
            "WHERE user_id IN " +
            "(SELECT friend_id FROM friendship WHERE user_id = ?)";


    @Autowired
    UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User getUser(int id) {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_USER_BY_ID, (rs, rowNum) -> makeUser(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(getMessageForUserNotFoundException(id));
        }
    }

    @Override
    public List<User> getUserList() {
        return jdbcTemplate.query(SQL_GET_USER_LIST, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User addUser(User newUser) {
        if (newUser.getId() == 0 && newUser.getFriends().isEmpty()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_ADD_NEW_USER, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, newUser.getLogin());
                ps.setString(2, newUser.getName());
                ps.setDate(3, java.sql.Date.valueOf(newUser.getBirthday()));
                ps.setString(4, newUser.getEmail());
                return ps;
                }, keyHolder
            );
            newUser.setId((int) keyHolder.getKey());
            log.info("Добавление нового пользователя - {}", newUser);
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
        User savedUser = getUser(userId);
        if (!savedUser.getFriends().equals(user.getFriends())) {
            log.warn("Ошибка обновления информации о пользователе. " +
                    "Список друзей пользователя был некорректно изменен");
            throw new ValidationException("Список друзей пользователя был некорректно изменен");
        }
        jdbcTemplate.update(SQL_UPDATE_USER,
                user.getLogin(), user.getName(), java.sql.Date.valueOf(user.getBirthday()), user.getEmail(), userId);
        return user;
    }

    @Override
    public void deleteUser(int id) {
        int result = jdbcTemplate.update(SQL_DELETE_USER_BY_ID, id);
        if (result == 1) {
            log.info("Удаление пользователя с id {}", id);
        } else {
            log.warn("Попытка удаления несуществующего пользователя с id - {}", id);
            throw new ResourceNotFoundException(getMessageForUserNotFoundException(id));
        }
    }

    @Override
    public void deleteUserList() {
        jdbcTemplate.update(SQL_DELETE_USER_LIST);
        log.info("Удаление списка пользователей");
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        HashSet<Integer> friends = new HashSet<>(getUserFriendList(id));
        return new User(id, email, login, name, birthday, friends);
    }

    private Integer makeFriendId(ResultSet rs) throws SQLException {
        return rs.getInt("friend_id");
    }

    private List<Integer> getUserFriendList(int id) {
        return jdbcTemplate.query(SQL_GET_USER_FRIENDS_ID_LIST, (rs, rowNum) -> makeFriendId(rs), id);
    }

    private String getMessageForUserNotFoundException(int userId) {
        return "Пользователя с id = " + userId + " не существует в БД";
    }

    public void addConfFriendship(int user_id, int friend_id) {
        jdbcTemplate.update(SQL_ADD_FRIEND_FOR_USER, user_id, friend_id, 1);
    }

    public void addUnconfFriendship(int user_id, int friend_id) {
        jdbcTemplate.update(SQL_ADD_FRIEND_FOR_USER, user_id, friend_id, 2);
    }

    public void removeFriendship(int userId, int friendId) {
        int result = jdbcTemplate.update(SQL_DELETE_USER_FRIEND, userId, friendId);
        if (result == 0) {
            log.warn("Попытка удаления из друзей пользователя с id {}, отсутствующего в друзьях у пользователя с id {}",
                    friendId, userId);
            throw new ResourceNotFoundException("Пользователь с id = " + friendId +
                    " отсутствует в друзьях у пользователя с id = " + userId);
        }
    }

    public List<User> getUserFriends(int userId) {
        return jdbcTemplate.query(SQL_GET_USER_FRIENDS_LIST, (rs, rowNum) -> makeUser(rs), userId);
    }
}
