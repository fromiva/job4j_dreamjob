package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {
    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepository() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oUserRepositoryTest.class
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearUsers() {
        Collection<User> users = sql2oUserRepository.findAll();
        for (User user : users) {
            sql2oUserRepository.deleteById(user.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        User user = new User(0, "user@mail.com", "Username", "password");
        User expected = sql2oUserRepository.save(user).get();
        User actual = sql2oUserRepository
                .findByEmailAndPassword(expected.getEmail(), expected.getPassword()).get();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void whenFindWithRightNameAndWrongPasswordThenNotFound() {
        User user = new User(0, "user@mail.com", "Username", "password");
        sql2oUserRepository.save(user);
        Optional<User> expected = Optional.empty();
        Optional<User> actual = sql2oUserRepository
                .findByEmailAndPassword(user.getEmail(), user.getPassword() + "123");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        User user1 = new User(0, "user1@mail.com", "Username1", "password1");
        User user2 = new User(0, "user2@mail.com", "Username2", "password2");
        User user3 = new User(0, "user3@mail.com", "Username3", "password3");
        sql2oUserRepository.save(user1);
        sql2oUserRepository.save(user2);
        sql2oUserRepository.save(user3);
        Collection<User> expected = List.of(user1, user2, user3);
        Collection<User> actual = sql2oUserRepository.findAll();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenUseTheSameEmailManyTimesThenEmptyOptional() {
        User user1 = new User(0, "user@mail.com", "Username1", "password1");
        User user2 = new User(0, "user@mail.com", "Username2", "password2");
        sql2oUserRepository.save(user1);
        Optional<User> expected = Optional.empty();
        Optional<User> actual = sql2oUserRepository.save(user2);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        Optional<User> expected = Optional.empty();
        Optional<User> actual = sql2oUserRepository
                .findByEmailAndPassword("user@mail.com", "Username");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDontSaveThenEmptyCollection() {
        Collection<User> expected = List.of();
        Collection<User> actual = sql2oUserRepository.findAll();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        User user = sql2oUserRepository.save(
                new User(0, "user@mail.com", "Username", "password")
                ).get();
        boolean result = sql2oUserRepository.deleteById(user.getId());
        Optional<User> expected = Optional.empty();
        Optional<User> actual = sql2oUserRepository
                .findByEmailAndPassword(user.getEmail(), user.getPassword());
        assertThat(result).isTrue();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        boolean result = sql2oUserRepository.deleteById(1);
        assertThat(result).isFalse();
    }
}
