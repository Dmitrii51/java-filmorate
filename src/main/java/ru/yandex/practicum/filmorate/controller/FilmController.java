package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Integer, Film> filmList = new HashMap<>();
    private int uniqueId;
    private static final LocalDate CINEMA_DAY = LocalDate.of(1895, 12, 28);
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @GetMapping
    public List<Film> getFilmList() {
        return new ArrayList<>(filmList.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody Film newFilm) {
        if (newFilm.getId() == 0 && validateFilm(newFilm)) {
            uniqueId += 1;
            newFilm.setId(uniqueId);
            filmList.put(uniqueId, newFilm);
            log.info("Добавление нового фильма c id " + newFilm.getId());
        }
        return newFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        int filmId = film.getId();
        if (filmList.containsKey(filmId) && validateFilm(film)) {
            filmList.put(filmId, film);
            log.info("Изменение информации о фильме с id " + film.getId());
        } else {
            throw new ValidationException("Указанного фильма не существует");
        }
        return film;
    }

    private boolean validateFilm(Film film) {
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            log.info("Ошибка валидации названия фильма - " + film.getName());
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH || film.getDescription().isEmpty()) {
            log.info("Ошибка валидации описания фильма - " + film.getDescription());
            throw new ValidationException("Описание фильма не может быть пустым и не должно быть больше 200 символов");
        } else if (film.getReleaseDate().isBefore(CINEMA_DAY)) {
            log.info("Ошибка валидации даты релиза фильма - " + film.getReleaseDate().format(Film.FORMATTER));
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            log.info("Ошибка валидации длительности фильма - " + film.getDuration());
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        } else {
            return true;
        }
    }
}
