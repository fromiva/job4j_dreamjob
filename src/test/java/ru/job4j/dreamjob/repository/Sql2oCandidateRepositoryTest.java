package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.*;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;

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

class Sql2oCandidateRepositoryTest {
    private static Sql2oCandidateRepository sql2oCandidateRepository;
    private static Sql2oFileRepository sql2oFileRepository;
    private static File file;

    @BeforeAll
    public static void initRepositories() throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = Sql2oCandidateRepositoryTest.class
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        String url = properties.getProperty("datasource.url");
        String username = properties.getProperty("datasource.username");
        String password = properties.getProperty("datasource.password");

        DatasourceConfiguration configuration = new DatasourceConfiguration();
        DataSource dataSource = configuration.connectionPool(url, username, password);
        Sql2o sql2o = configuration.databaseClient(dataSource);

        sql2oCandidateRepository = new Sql2oCandidateRepository(sql2o);
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
        Collection<Candidate> candidates = sql2oCandidateRepository.findAll();
        for (Candidate candidate : candidates) {
            sql2oCandidateRepository.deleteById(candidate.getId());
        }
    }

    @Test
    public void whenSaveThenGetSame() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate expected = sql2oCandidateRepository.save(new Candidate(0, "name",
                "description", creationDate, 1, file.getId()));
        Candidate actual = sql2oCandidateRepository.findById(expected.getId()).get();
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void whenSaveSeveralThenGetAll() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate1 = sql2oCandidateRepository.save(new Candidate(0, "name1",
                "description1", creationDate, 1, file.getId()));
        Candidate candidate2 = sql2oCandidateRepository.save(new Candidate(0, "name2",
                "description2", creationDate, 3, file.getId()));
        Candidate candidate3 = sql2oCandidateRepository.save(new Candidate(0, "name3",
                "description3", creationDate, 2, file.getId()));
        Collection<Candidate> expected = List.of(candidate1, candidate2, candidate3);
        Collection<Candidate> actual = sql2oCandidateRepository.findAll();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDontSaveThenNothingFound() {
        Optional<Candidate> expected = Optional.empty();
        Optional<Candidate> actual = sql2oCandidateRepository.findById(1);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDontSaveThenEmptyCollection() {
        Collection<Candidate> expected = List.of();
        Collection<Candidate> actual = sql2oCandidateRepository.findAll();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDeleteThenGetEmptyOptional() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = sql2oCandidateRepository.save(new Candidate(0, "name",
                "description", creationDate, 1, file.getId()));
        boolean result = sql2oCandidateRepository.deleteById(candidate.getId());
        Optional<Candidate> expected = Optional.empty();
        Optional<Candidate> actual = sql2oCandidateRepository.findById(candidate.getId());
        assertThat(result).isTrue();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenDeleteByInvalidIdThenGetFalse() {
        boolean result = sql2oCandidateRepository.deleteById(1);
        assertThat(result).isFalse();
    }

    @Test
    public void whenUpdateThenGetUpdated() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = sql2oCandidateRepository.save(new Candidate(0, "name",
                "description", creationDate, 1, file.getId()));
        Candidate expected = new Candidate(candidate.getId(), "new name",
                "new description", creationDate.plusDays(1), 1, file.getId());
        boolean result = sql2oCandidateRepository.update(expected);
        Candidate actual = sql2oCandidateRepository.findById(candidate.getId()).get();
        assertThat(result).isTrue();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenUpdateUnExistingVacancyThenGetFalse() {
        LocalDateTime creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Candidate candidate = new Candidate(0, "name",
                "description", creationDate, 1, file.getId());
        boolean result = sql2oCandidateRepository.update(candidate);
        assertThat(result).isFalse();
    }
}
