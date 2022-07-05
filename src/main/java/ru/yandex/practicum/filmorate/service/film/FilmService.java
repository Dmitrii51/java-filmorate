package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

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

    public Film addLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        userStorage.getUser(userId);
        filmStorage.updateFilm(film);
        return film;
    }

    public Film removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilm(filmId);
        userStorage.getUser(userId);
        filmStorage.updateFilm(film);
        return film;
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
