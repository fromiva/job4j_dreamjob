package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@ThreadSafe
@Controller
@RequestMapping("/users")
public final class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegistrationPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return "redirect:/index";
        }
        user = new User();
        user.setName("Гость");
        model.addAttribute("user", user);
        return "users/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        Optional<User> result = userService.save(user);
        if (result.isEmpty()) {
            model.addAttribute("user", new User(0, null, "Гость", null));
            model.addAttribute("message",
                    "Ошибка регистрации. Пользователь с указанной почтой уже существует.");
            return "users/register";
        }
        return "redirect:login";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return "redirect:/index";
        }
        user = new User();
        user.setName("Гость");
        model.addAttribute("user", user);
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest request) {
        Optional<User> userOptional = userService
                .findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (userOptional.isEmpty()) {
            model.addAttribute("user", new User(0, null, "Гость", null));
            model.addAttribute("error", "Почта или пароль введены неверно");
            return "users/login";
        }
        request.getSession().setAttribute("user", userOptional.get());
        return "redirect:/vacancies";
    }

    @GetMapping("/logout")
    public String logoutUser(Model model, HttpSession session) {
        session.invalidate();
        model.addAttribute("user", new User(0, null, "Гость", null));
        return "redirect:/users/login";
    }
}
