package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.Vacancy;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oVacancyRepositoryTest {
    private static Sql2oVacancyRepository sql2oVacancyRepository;
    private static Sql2oFileRepository sql2oFileRepository;
    private static File file;

    @BeforeAll
    public static void initRepositories() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oVacancyRepositoryTest.class
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);

        sql2oVacancyRepository = new Sql2oVacancyRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    @AfterAll
    public static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    public void clearVacancies() {
        Collection<Vacancy> vacancies = sql2oVacancyRepository.findAll();
        for (Vacancy vacancy : vacancies) {
            sql2oVacancyRepository.deleteById(vacancy.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Vacancy expected = sql2oVacancyRepository.save(
                new Vacancy(0, "title", "description", creationDate, true, 1, file.getId()));
        Vacancy actual = sql2oVacancyRepository.findById(expected.getId()).get();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Vacancy vacancy1 = sql2oVacancyRepository.save(
                new Vacancy(0, "title1", "description1", creationDate, true, 1, file.getId()));
        Vacancy vacancy2 = sql2oVacancyRepository.save(
                new Vacancy(0, "title2", "description2", creationDate, false, 1, file.getId()));
        Vacancy vacancy3 = sql2oVacancyRepository.save(
                new Vacancy(0, "title3", "description3", creationDate, true, 1, file.getId()));
        Collection<Vacancy> expected = List.of(vacancy1, vacancy2, vacancy3);
        Collection<Vacancy> actual = sql2oVacancyRepository.findAll();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        Optional<Vacancy> expected = Optional.empty();
        Optional<Vacancy> actual = sql2oVacancyRepository.findById(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDontSaveThenEmptyCollection() {
        Collection<Vacancy> expected = List.of();
        Collection<Vacancy> actual = sql2oVacancyRepository.findAll();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Vacancy vacancy = sql2oVacancyRepository.save(
                new Vacancy(0, "title", "description", creationDate, true, 1, file.getId()));
        boolean result = sql2oVacancyRepository.deleteById(vacancy.getId());
        Optional<Vacancy> expected = Optional.empty();
        Optional<Vacancy> actual = sql2oVacancyRepository.findById(vacancy.getId());
        assertThat(result).isTrue();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        boolean result = sql2oVacancyRepository.deleteById(1);
        assertThat(result).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Vacancy vacancy = sql2oVacancyRepository.save(new Vacancy(0, "title",
                "description", creationDate, true, 1, file.getId()));
        Vacancy expected = new Vacancy(vacancy.getId(), "new title", "new description",
                creationDate.plusDays(1), !vacancy.getVisible(), 1, file.getId());
        boolean result = sql2oVacancyRepository.update(expected);
        Vacancy actual = sql2oVacancyRepository.findById(vacancy.getId()).get();
        assertThat(result).isTrue();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenUpdateUnExistingVacancyThenGetFalse() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Vacancy vacancy = new Vacancy(0, "title",
                "description", creationDate, true, 1, file.getId());
        boolean result = sql2oVacancyRepository.update(vacancy);
        assertThat(result).isFalse();
    }
}
