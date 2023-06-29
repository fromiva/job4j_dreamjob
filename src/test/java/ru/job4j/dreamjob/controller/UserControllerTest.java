package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserControllerTest {
    private UserService userService;
    private UserController userController;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void whenRequestUserRegistrationPageThenGetPage() {
        String view = userController.getRegistrationPage();
        assertThat(view).isEqualTo("users/register");
    }

    @Test
    void whenRequestLoginPageThenGetPage() {
        String view = userController.getLoginPage();
        assertThat(view).isEqualTo("users/login");
    }

    @Test
    void whenUserRegisterThenCaptureUser() {
        User expected = new User(0, "mail@mail.net", "Name", "password");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userService.save(captor.capture())).thenReturn(Optional.of(expected));

        Model model = new ConcurrentModel();
        String view = userController.register(expected, model);
        User actual = captor.getValue();

        assertThat(view).isEqualTo("redirect:login");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void whenUserLoginThenGetCredentials() {
        String expectedEmail = "mail@mail.net";
        String expectedPassword = "password";
        User user = new User(0, expectedEmail, "Name", expectedPassword);
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> passwordCaptor = ArgumentCaptor.forClass(String.class);
        when(userService.findByEmailAndPassword(emailCaptor.capture(), passwordCaptor.capture()))
                .thenReturn(Optional.of(user));

        Model model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        String view = userController.loginUser(user, model, request);

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(emailCaptor.getValue()).isEqualTo(expectedEmail);
        assertThat(passwordCaptor.getValue()).isEqualTo(expectedPassword);
    }

    @Test
    void whenUserNoLoginThenGetErrorMessage() {
        User user = new User(0, "mail@mail.net", "Name", "password");
        when(userService.findByEmailAndPassword(any(String.class), any(String.class)))
                .thenReturn(Optional.empty());

        Model model = new ConcurrentModel();
        HttpServletRequest request = new MockHttpServletRequest();
        String view = userController.loginUser(user, model, request);
        Object message = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(message).isEqualTo("Почта или пароль введены неверно");
    }
}
