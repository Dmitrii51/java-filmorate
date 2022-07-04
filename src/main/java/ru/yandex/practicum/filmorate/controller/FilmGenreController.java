package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.FilmGenreStorage;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class FilmGenreController {
    private final FilmGenreStorage filmGenreStorage;

    @Autowired
    public FilmGenreController(FilmGenreStorage filmGenreStorage) {
        this.filmGenreStorage = filmGenreStorage;
    }

    @GetMapping("/{genreId}")
    public Genre getFilmGenre(@PathVariable int genreId) {
        return filmGenreStorage.getFilmGenre(genreId);
    }

    @GetMapping
    public List<Genre> getFilmGenreList() {
        return filmGenreStorage.getFilmGenreList();
    }
}
