package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.model.util.validator.After;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film implements Comparable<Film> {
    private final static String DATE_PATTERN = "yyyy-MM-dd";
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private final static String CINEMA_DAY = "1895-12-28";
    private final static int MIN_DESCRIPTION_LENGTH = 1;
    private final static int MAX_DESCRIPTION_LENGTH = 200;
    private Set<Integer> likes;
    private Set<Genre> genres;
    private int id;
    @Positive
    private int duration;

    @NotBlank(message = "Название фильма не может состоять только из пробелов")
    @NotEmpty(message = "Название фильма не может быть пустым")
    private String name;

    @NotBlank(message = "Описание фильма не может состоять только из пробелов")
    @Size(min = MIN_DESCRIPTION_LENGTH, max = MAX_DESCRIPTION_LENGTH,
            message = "Описание фильма не должно быть больше 200 символов")
    private String description;

    @DateTimeFormat(pattern = DATE_PATTERN)
    @After(value = CINEMA_DAY, message = "Дата релиза фильма не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    private Mpa mpa;

    private int rate;

    @JsonCreator
    public Film(String name, String description, String releaseDate, int duration, int rate, Mpa mpaRating) {
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate, FORMATTER);
        this.duration = duration;
        this.likes = new HashSet<>();
        this.mpa = mpaRating;
        this.rate = rate;
    }

    public Film(int id, String name, String description, int duration, int rate,
                LocalDate releaseDate, Mpa mpaRating, HashSet<Integer> likes, HashSet<Genre> genres) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.mpa = mpaRating;
        this.likes = likes;
        this.genres = genres;
        this.rate = rate;
    }

    public Film(int id, String name, String description, int duration, int rate,
                LocalDate releaseDate, Mpa mpaRating, HashSet<Integer> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.releaseDate = releaseDate;
        this.mpa = mpaRating;
        this.likes = likes;
        this.rate = rate;
    }

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void removeLike(int userId) {
        likes.removeIf(id -> (id == userId));
    }

    @Override
    public int compareTo(Film otherFilm) {
        if (otherFilm.getLikes().size() == this.getLikes().size()) {
            return otherFilm.getId() - this.getId();
        }
        return otherFilm.getLikes().size() - this.getLikes().size();
    }
}
