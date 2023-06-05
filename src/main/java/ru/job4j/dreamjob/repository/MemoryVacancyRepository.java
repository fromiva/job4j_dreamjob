package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryVacancyRepository implements VacancyRepository {
    private final Map<Integer, Vacancy> vacancies = new HashMap<>();
    private int nextId = 1;

    private MemoryVacancyRepository() {
        save(new Vacancy(0,
                "Intern Java Developer",
                "Intern Java Developer vacancy description",
                LocalDateTime.now().minusDays(6)));
        save(new Vacancy(0,
                "Junior Java Developer",
                "Junior Java Developer vacancy description",
                LocalDateTime.now().minusDays(5)));
        save(new Vacancy(0,
                "Junior+ Java Developer",
                "Junior+ Java Developer vacancy description",
                LocalDateTime.now().minusDays(4)));
        save(new Vacancy(0,
                "Middle Java Developer",
                "Middle Java Developer vacancy description",
                LocalDateTime.now().minusDays(3)));
        save(new Vacancy(0,
                "Middle+ Java Developer",
                "Middle+ Java Developer vacancy description",
                LocalDateTime.now().minusDays(2)));
        save(new Vacancy(0,
                "Senior Java Developer",
                "Senior Java Developer vacancy description",
                LocalDateTime.now().minusDays(1)));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(),
                (id, oldVacancy) -> new Vacancy(
                        oldVacancy.getId(),
                        vacancy.getTitle(),
                        vacancy.getDescription(),
                        vacancy.getCreationDate()
                )) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }
}
