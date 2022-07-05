package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaFilmRatingStorage {

    Mpa getMpaFilmRating(int id);

    List<Mpa> getMpaFilmRatingList();
}
