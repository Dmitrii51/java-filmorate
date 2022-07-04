package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String SQL_GET_GENRE_BY_ID = "SELECT * FROM genre " +
            "WHERE genre_id = ?";
    private final String SQL_GET_GENRE_LIST = "SELECT * FROM genre";

    @Autowired
    public FilmGenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getFilmGenre(int id) {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_GENRE_BY_ID, (rs, rowNum) -> makeFilmGenre(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Жанра фильмов с id = " + id + " не существует");
        }
    }

    @Override
    public List<Genre> getFilmGenreList() {
        return jdbcTemplate.query(SQL_GET_GENRE_LIST, (rs, rowNum) -> makeFilmGenre(rs));
    }

    private Genre makeFilmGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("genre_id");
        String title = rs.getString("title");
        return new Genre(id, title);
    }
}
