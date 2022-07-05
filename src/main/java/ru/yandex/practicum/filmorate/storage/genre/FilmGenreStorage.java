package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface FilmGenreStorage {
    Genre getFilmGenre(int id);

    List<Genre> getFilmGenreList();
}
