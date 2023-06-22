package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.VacancyRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public final class SimpleVacancyService implements VacancyService {

    private final VacancyRepository vacancyRepository;
    private final FileService fileService;

    public SimpleVacancyService(VacancyRepository vacancyRepository,
                                FileService fileService) {
        this.vacancyRepository = vacancyRepository;
        this.fileService = fileService;
    }

    @Override
    public Vacancy save(Vacancy vacancy, FileDto image) {
        saveNewFile(vacancy, image);
        return vacancyRepository.save(vacancy);
    }

    private void saveNewFile(Vacancy vacancy, FileDto image) {
        File file = fileService.save(image);
        vacancy.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Vacancy> vacancyOptional = findById(id);
        if (vacancyOptional.isEmpty()) {
            return false;
        }
        boolean result = vacancyRepository.deleteById(id);
        fileService.deleteById(vacancyOptional.get().getFileId());
        return result;
    }

    @Override
    public boolean update(Vacancy vacancy, FileDto image) {
        if (image.getContent().length == 0) {
            return vacancyRepository.update(vacancy);
        }
        int oldFileId = vacancy.getFileId();
        saveNewFile(vacancy, image);
        boolean result = vacancyRepository.update(vacancy);
        fileService.deleteById(oldFileId);
        return result;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return vacancyRepository.findById(id);
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }
}
