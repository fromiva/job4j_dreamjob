package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.model.Vacancy;

import java.util.Collection;
import java.util.Optional;

@Repository
public class Sql2oVacancyRepository implements VacancyRepository {
    private final Sql2o sql2o;

    public Sql2oVacancyRepository(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                      INSERT INTO
                      vacancies(title, description, creation_date, visible, city_id, file_id)
                      VALUES (:title, :description, :creationDate, :visible, :cityId, :fileId)
                      """;
            Query query = connection.createQuery(sql, true)
                    .addParameter("title", vacancy.getTitle())
                    .addParameter("description", vacancy.getDescription())
                    .addParameter("creationDate", vacancy.getCreationDate())
                    .addParameter("visible", vacancy.getVisible())
                    .addParameter("cityId", vacancy.getCityId())
                    .addParameter("fileId", vacancy.getFileId());
            int generatedId = query.executeUpdate().getKey(Integer.class);
            vacancy.setId(generatedId);
            return vacancy;
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("delete from vacancies where id=:id")
                    .addParameter("id", id);
            int affectedRows = query.executeUpdate().getResult();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean update(Vacancy vacancy) {
        try (Connection connection = sql2o.open()) {
            String sql = """
                    UPDATE vacancies
                    SET title = :title, description = :description, creation_date = :creationDate,
                        visible = :visible, city_id = :cityId, file_id = :fileId
                    WHERE id = :id
                    """;
            Query query = connection.createQuery(sql)
                    .addParameter("title", vacancy.getTitle())
                    .addParameter("description", vacancy.getDescription())
                    .addParameter("creationDate", vacancy.getCreationDate())
                    .addParameter("visible", vacancy.getVisible())
                    .addParameter("cityId", vacancy.getCityId())
                    .addParameter("fileId", vacancy.getFileId())
                    .addParameter("id", vacancy.getId());
            int affectedRows = query.executeUpdate().getResult();
            return affectedRows > 0;
        }
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("select * from vacancies where id=:id")
                    .addParameter("id", id)
                    .setColumnMappings(Vacancy.COLUMN_MAPPING);
            Vacancy vacancy = query.executeAndFetchFirst(Vacancy.class);
            return Optional.ofNullable(vacancy);
        }
    }

    @Override
    public Collection<Vacancy> findAll() {
        try (Connection connection = sql2o.open()) {
            Query query = connection.createQuery("select * from vacancies")
                    .setColumnMappings(Vacancy.COLUMN_MAPPING);
            return query.executeAndFetch(Vacancy.class);
        }
    }
}
