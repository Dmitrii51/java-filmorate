package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaFilmRatingStorage;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class MpaFilmRatingController {
    private final MpaFilmRatingStorage mpaFilmRatingStorage;

    @Autowired
    public MpaFilmRatingController(MpaFilmRatingStorage mpaFilmRatingStorage) {
        this.mpaFilmRatingStorage = mpaFilmRatingStorage;
    }

    @GetMapping("/{mpaId}")
    public Mpa getMpaFilmRating(@PathVariable int mpaId) {
        return mpaFilmRatingStorage.getMpaFilmRating(mpaId);
    }

    @GetMapping
    public List<Mpa> getMpaFilmRatingList() {
        return mpaFilmRatingStorage.getMpaFilmRatingList();
    }
}
