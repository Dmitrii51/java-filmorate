package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable int filmId) {
        return filmStorage.getFilm(filmId);
    }

    @GetMapping
    public List<Film> getFilmList() {
        return filmStorage.getFilmList();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") String count) {
        int countToInt = Integer.parseInt(count);
        return filmService.getPopularFilms(countToInt);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film newFilm) {
        return filmStorage.addFilm(newFilm);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film dislikeFilm(@PathVariable int id, @PathVariable int userId) {
        return filmService.removeLike(id, userId);
    }
}
