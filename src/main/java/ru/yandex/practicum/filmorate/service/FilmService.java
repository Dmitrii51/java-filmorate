package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.List;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        userStorage.getUser(userId);
        film.addLike(userId);
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        userStorage.getUser(userId);
        film.removeLike(userId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> filmList = filmStorage.getFilmList();
        Collections.sort(filmList);
        if (filmList.size() > count) {
            filmList = filmList.subList(0, count);
        }
        return filmList;
    }
}
