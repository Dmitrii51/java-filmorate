package ru.yandex.practicum.filmorate.storage.film;

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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Component
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final String SQL_GET_FILM_BY_ID = "SELECT *, " +
            "mpa.mpa_rating_id AS mpa_id, mpa.name AS mpa_name " +
            "FROM films " +
            "LEFT JOIN mpa_rating AS mpa ON films.mpa_rating_id = mpa.mpa_rating_id " +
            "WHERE film_id = ?";
    private final String SQL_GET_FILM_LIST = "SELECT *, mpa.mpa_rating_id AS mpa_id, mpa.name AS mpa_name " +
            "FROM films " +
            "LEFT JOIN mpa_rating AS mpa ON films.mpa_rating_id = mpa.mpa_rating_id ";
    private final String SQL_GET_FILM_GENRES_LIST  = "SELECT film_genres.genre_id, genre.title FROM film_genres " +
            "LEFT JOIN genre ON film_genres.genre_id = genre.genre_id " +
            "WHERE film_id = ? " +
            "ORDER BY genre_id";
    private final String SQL_GET_FILM_LIKES_LIST = "SELECT user_id FROM likes " +
            "WHERE film_id = ?";
    private final String SQL_ADD_NEW_FILM = "INSERT INTO films (name, description, release_date, " +
            "duration, rate, mpa_rating_id) VALUES (?, ?, ?, ?, ?, ?)";
    private final String SQL_INSERT_FILM_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private final String SQL_UPDATE_FILM = "UPDATE films " +
            "SET (name, description, release_date, duration, mpa_rating_id, rate) = VALUES (?, ?, ?, ?, ?, ?) " +
            "WHERE film_id = ?";
    private final String SQL_DELETE_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    private final String SQL_INSERT_FILM_LIKES = "INSERT INTO likes (film_id, user_id) VALUES(?, ?)";
    private final String SQL_DELETE_LIKES = "DELETE FROM likes " +
            "WHERE film_id = ?";


    @Autowired
    FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film getFilm(int id) {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_FILM_BY_ID, (rs, rowNum) -> makeFilm(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(getMessageForFilmNotFoundException(id));
        }
    }

    @Override
    public List<Film> getFilmList() {
        return jdbcTemplate.query(SQL_GET_FILM_LIST, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film addFilm(Film newFilm) {
        if (newFilm.getId() == 0 && newFilm.getLikes().isEmpty()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(SQL_ADD_NEW_FILM,
                                Statement.RETURN_GENERATED_KEYS);
                        ps.setString(1, newFilm.getName());
                        ps.setString(2, newFilm.getDescription());
                        ps.setDate(3, java.sql.Date.valueOf(newFilm.getReleaseDate()));
                        ps.setInt(4, newFilm.getDuration());
                        ps.setInt(5, newFilm.getRate());
                        ps.setInt(6, newFilm.getMpa().getId());
                        return ps;
                    }, keyHolder
            );
            newFilm.setId((int) keyHolder.getKey());
            insertFilmGenres(newFilm);
            log.info("Добавление нового фильма - {}", newFilm);
        } else {
            log.warn("Добавление фильма с некорректными параметрами - {}", newFilm);
            throw new ValidationException("Ошибка добавления фильма. " +
                    "Количество лайков и id должно быть равно 0");
        }
        return newFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        Film savedFilm = getFilm(filmId);
        if (!savedFilm.getLikes().equals(film.getLikes())) {
            if (!savedFilm.getLikes().equals(film.getLikes())) {
                log.warn("Ошибка обновления информации о фильме. " +
                        "Список пользователей, которым понравился фильм, был некорректно изменен");
                throw new ValidationException("Список пользователей, которым понравился фильм был некорректно изменен");
            }
        }
        jdbcTemplate.update(SQL_UPDATE_FILM,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getRate(), filmId);
        jdbcTemplate.update(SQL_DELETE_GENRES, filmId);
        jdbcTemplate.update(SQL_DELETE_LIKES, filmId);
        insertFilmGenres(film);
        insertFilmLikes(film);
        log.info("Изменение информации о фильме с id {}", film.getId());
        Film film1 = getFilm(filmId);
        if (film.getGenres() != null && film.getGenres().isEmpty()) {
            film1.setGenres(new HashSet<>());
        } else if (film.getGenres() == null) {
            film1.setGenres(null);
        }
        return film1;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        int duration = rs.getInt("duration");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int rate = rs.getInt("rate");
        Mpa mpaRating = new Mpa(
                rs.getInt("mpa_id"),
                rs.getString("mpa_name"));
        HashSet<Integer> likes = new HashSet<>(getFilmLikes(id));
        HashSet<Genre> genres = new HashSet<>(getFilmGenres(id));
        if (genres.isEmpty()) {
            genres = null;
        }
        return new Film(id,  name,  description, duration, rate, releaseDate, mpaRating, likes, genres);
    }

    private Genre makeFilmGenresList(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(rs.getInt("genre_id"), rs.getString("title"));
    }

    private Integer makeFilmLikes(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("user_id");
    }

    private String getMessageForFilmNotFoundException(int filmId) {
        return "Фильма с id = " + filmId + " не существует в БД";
    }

    private List<Integer> getFilmLikes(int filmId) {
        return jdbcTemplate.query(SQL_GET_FILM_LIKES_LIST, this::makeFilmLikes, filmId);
    }

    private List<Genre> getFilmGenres(int filmId) {
        return jdbcTemplate.query(SQL_GET_FILM_GENRES_LIST, this::makeFilmGenresList, filmId);
    }

    private void insertFilmGenres(Film film) {
        if (film.getGenres() != null) {
            for (Genre filmGenre : film.getGenres()) {
                jdbcTemplate.update(SQL_INSERT_FILM_GENRES, film.getId(), filmGenre.getId());
            }
        }
    }

    private void insertFilmLikes(Film film) {
        for (Integer userId : film.getLikes()) {
            jdbcTemplate.update(SQL_INSERT_FILM_LIKES, film.getId(), userId);
        }
    }
}
