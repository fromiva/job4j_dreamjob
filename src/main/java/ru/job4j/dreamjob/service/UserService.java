package ru.job4j.dreamjob.service;

import ru.job4j.dreamjob.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {
    Optional<User> save(User user);

    boolean deleteById(int id);

    Optional<User> findByEmailAndPassword(String email, String password);

    Collection<User> findAll();
}
