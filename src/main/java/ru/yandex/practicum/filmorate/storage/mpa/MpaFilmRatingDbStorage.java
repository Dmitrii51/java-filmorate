package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaFilmRatingDbStorage implements MpaFilmRatingStorage {
    private final JdbcTemplate jdbcTemplate;
    private final String SQL_GET_MPA_RATING_BY_ID = "SELECT * FROM mpa_rating " +
            "WHERE mpa_rating_id = ?";
    private final String SQL_GET_MPA_RATING_LIST = "SELECT * FROM mpa_rating";

    @Autowired
    public MpaFilmRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getMpaFilmRating(int id) {
        try {
            return jdbcTemplate.queryForObject(SQL_GET_MPA_RATING_BY_ID, (rs, rowNum) -> makeMpaFilmRating(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("MPA-рейтинга с id = " + id + " не существует");
        }
    }

    @Override
    public List<Mpa> getMpaFilmRatingList() {
        return jdbcTemplate.query(SQL_GET_MPA_RATING_LIST, (rs, rowNum) -> makeMpaFilmRating(rs));
    }

    private Mpa makeMpaFilmRating(ResultSet rs) throws SQLException {
        int id = rs.getInt("mpa_rating_id");
        String name = rs.getString("name");
        return new Mpa(id, name);
    }
}
