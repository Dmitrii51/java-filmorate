package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class FilmControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createFilm() throws Exception {
        Film newFilm = new Film("TestFilm",
                "Two astronomers go on a media tour to warn humankind of a planet-killing comet hurtling " +
                        "toward Earth. The response from a distracted world: " + "Mehhhhhhhhhhhhhhhhhhhhhhhhhhh" +
                        "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhh.",
                "2021-12-05", 120);
        String body = mapper.writeValueAsString(newFilm);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateFilm() throws Exception {
        Film newFilm = new Film("TestFilm",
                "Two astronomers go on a media tour to warn humankind of a planet-killing comet hurtling " +
                        "toward Earth. The response from a distracted world: " + "Mehhhhhhhhhhhhhhhhhhhhhhhhhhh" +
                        "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhh.",
                "2021-12-05", 120);
        String body1 = mapper.writeValueAsString(newFilm);
        this.mockMvc.perform(post("/films").content(body1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        newFilm.setId(1);
        newFilm.setName("SuperTestFilm");
        String body2 = mapper.writeValueAsString(newFilm);
        this.mockMvc.perform(put("/films").content(body2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getFilmList() throws Exception {
        this.mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Film newFilm1 = new Film("TestFilm1", "Just good film",
                "2000-12-27", 120);
        Film newFilm2 = new Film("TestFilm2",
                "Two astronomers go on a media tour to warn humankind of a planet-killing comet hurtling " +
                        "toward Earth. The response from a distracted world: " + "Mehhhhhhhhhhhhhhhhhhhhhhhhhhh" +
                        "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhh.",
                "2021-12-05", 50);
        String body1 = mapper.writeValueAsString(newFilm1);
        String body2 = mapper.writeValueAsString(newFilm2);
        this.mockMvc.perform(post("/films").content(body1).contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(post("/films").content(body2).contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(get("/films").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateNonexistentFilm() throws Exception {
        Film film = new Film("Test Film", "Good film",
                "2005-12-27", 120);
        String body = mapper.writeValueAsString(film);
        this.mockMvc.perform(put("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("Указанного фильма не существует",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void createFilmWithoutName() throws Exception {
        Film filmWithoutName = new Film("", "Good film",
                "2005-12-27", 120);
        String body = mapper.writeValueAsString(filmWithoutName);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Название фильма не может быть пустым",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void createFilmWithNegativeDuration() throws Exception {
        Film filmWithNegativeDuration = new Film("TestFilm", "Good film",
                "2005-12-27", -120);
        String body = mapper.writeValueAsString(filmWithNegativeDuration);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Длительность фильма не может быть отрицательной",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void createFilmWithWrongSizeDescription() throws Exception {
        Film filmWithWrongSizeDescription1 = new Film("TestFilm", "",
                "2005-12-27", 120);
        String body1 = mapper.writeValueAsString(filmWithWrongSizeDescription1);
        this.mockMvc.perform(post("/films").content(body1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Описание фильма не может быть пустым и не должно быть больше 200 символов",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        Film filmWithWrongSizeDescription2 = new Film("TestFilm",
                "Two astronomers go on a media tour to warn humankind of a planet-killing comet hurtling " +
                        "toward Earth. The response from a distracted world: " + "Mehhhhhhhhhhhhhhhhhhhhhhhhhhh" +
                        "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh.",
                "2021-12-05", 120);
        String body2 = mapper.writeValueAsString(filmWithWrongSizeDescription2);
        this.mockMvc.perform(post("/films").content(body2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Описание фильма не может быть пустым и не должно быть больше 200 символов",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void createFilmWithWrongReleaseDate() throws Exception {
        Film filmWithWrongReleaseDate = new Film("TestFilm", "Just good film",
                "1895-12-27", 120);
        String body = mapper.writeValueAsString(filmWithWrongReleaseDate);
        this.mockMvc.perform(post("/films").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Дата релиза фильма не может быть раньше 28 декабря 1895 года",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}
