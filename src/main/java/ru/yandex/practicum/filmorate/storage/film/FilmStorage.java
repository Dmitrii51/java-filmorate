package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    Film getFilm(int id);

    List<Film> getFilmList();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    //void deleteFilm(int id);

    //void deleteFilmList();
}
