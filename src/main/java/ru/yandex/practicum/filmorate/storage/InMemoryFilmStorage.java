package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ResourceNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);
    private final HashMap<Integer, Film> filmList;
    private int uniqueId;

    public InMemoryFilmStorage() {
        filmList = new HashMap<>();
    }

    @Override
    public Film getFilm(int id) {
        if (filmList.containsKey(id)) {
            return filmList.get(id);
        }
        throw new ResourceNotFoundException(getMessageForFilmNotFoundException(id));
    }

    @Override
    public List<Film> getFilmList() {
        return new ArrayList<>(filmList.values());
    }

    @Override
    public Film addFilm(Film newFilm) {
        if (newFilm.getId() == 0 && newFilm.getLikes().isEmpty()) {
            uniqueId += 1;
            newFilm.setId(uniqueId);
            filmList.put(newFilm.getId(), newFilm);
            log.info("Добавление нового фильма c id {}", newFilm.getId());
        } else {
            log.warn("Добавление фильма с некорректными параметрами - {}", newFilm);
            throw new ValidationException("Ошибка добавления фильма. " +
                    "Список пользователей, которым понравился фильм, должен быть пуст, а id = 0");
        }
        return newFilm;
    }

    @Override
    public Film updateFilm(Film film) {
        int filmId = film.getId();
        if (filmList.containsKey(filmId)) {
            Film savedFilm = filmList.get(filmId);
            if (!savedFilm.getLikes().equals(film.getLikes())) {
                log.warn("Ошибка обновления информации о фильме. " +
                        "Список пользователей, которым понравился фильм, был некорректно изменен");
                throw new ValidationException("Список пользователей, которым понравился фильм был некорректно изменен");
            }
            filmList.put(filmId, film);
            log.info("Изменение информации о фильме с id {}", film.getId());
        } else {
            log.warn("Попытка обновления несуществующего фильма - {}", film);
            throw new ResourceNotFoundException(getMessageForFilmNotFoundException(filmId));
        }
        return film;
    }

    @Override
    public void deleteFilm(int id) {
        if (filmList.containsKey(id)) {
            filmList.remove(id);
        } else {
            log.warn("Попытка удаления несуществующего фильма с id - {}", id);
            throw new ResourceNotFoundException(getMessageForFilmNotFoundException(id));
        }
    }

    @Override
    public void deleteFilmList() {
        filmList.clear();
    }

    private String getMessageForFilmNotFoundException(int filmId) {
        return "Фильма с id = " + filmId + " не существует";
    }
}
