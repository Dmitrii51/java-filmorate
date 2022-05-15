package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void createUser() throws Exception {
        User user = new User("bubble@yandex.ru", "SuperUser",
                "James Bubble", "1995-12-05");
        String body = mapper.writeValueAsString(user);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUser() throws Exception {
        User newUser = new User("bubble@yandex.ru", "SuperUser",
                "James Bubble", "1995-12-05");
        String body1 = mapper.writeValueAsString(newUser);
        this.mockMvc.perform(post("/users").content(body1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        newUser.setId(1);
        newUser.setEmail("bubblemaster@yandex.ru");
        String body2 = mapper.writeValueAsString(newUser);
        this.mockMvc.perform(put("/users").content(body2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserList() throws Exception {
        this.mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User newUser1 = new User("bubble@yandex.ru", "SuperUser",
                "James Bubble", "1995-12-05");
        User newUser2 = new User("parsons@yandex.ru", "TestUser",
                "Mike Parsons", "2002-12-05");
        String body1 = mapper.writeValueAsString(newUser1);
        String body2 = mapper.writeValueAsString(newUser2);
        this.mockMvc.perform(post("/users").content(body1).contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(post("/users").content(body2).contentType(MediaType.APPLICATION_JSON));
        this.mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateNonexistentUser() throws Exception {
        User user = new User("bubble@yandex.ru", "SuperUser",
                "James Bubble", "1995-12-05");
        String body = mapper.writeValueAsString(user);
        this.mockMvc.perform(put("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("Указанного пользователя не существует",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void createUserWithoutName() throws Exception {
        User userWithoutName = new User("bubble@yandex.ru", "SuperUser",
                "", "1995-12-05");
        String body = mapper.writeValueAsString(userWithoutName);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createUserFromFuture() throws Exception {
        User userFromFuture = new User("bubble@yandex.ru", "SuperUser",
                "", "2100-12-05");
        String body = mapper.writeValueAsString(userFromFuture);
        this.mockMvc.perform(post("/users").content(body).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Дата рождения не может быть в будущем",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void createUserWithoutLogin() throws Exception {
        User userWithoutLogin1 = new User("bubble@yandex.ru", "",
                "", "1995-12-05");
        String body1 = mapper.writeValueAsString(userWithoutLogin1);
        this.mockMvc.perform(post("/users").content(body1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Логин не может быть пустым и содержать пробелы",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        User userWithoutLogin2 = new User("bubble@yandex.ru", "  ",
                "Mike Bubble", "1995-12-07");
        String body2 = mapper.writeValueAsString(userWithoutLogin2);
        this.mockMvc.perform(post("/users").content(body2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Логин не может быть пустым и содержать пробелы",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    public void createUserWithWrongEmail() throws Exception {
        User userWithWrongEmail1 = new User("bubbleyandex.ru", "SuperUser",
                "", "2100-12-05");
        String body1 = mapper.writeValueAsString(userWithWrongEmail1);
        this.mockMvc.perform(post("/users").content(body1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Электронная почта не может быть пустой и должна содержать символ @",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));

        User userWithWrongEmail2 = new User("", "SuperUser",
                "", "2100-12-05");
        String body2 = mapper.writeValueAsString(userWithWrongEmail2);
        this.mockMvc.perform(post("/users").content(body2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals(
                        "Электронная почта не может быть пустой и должна содержать символ @",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}
