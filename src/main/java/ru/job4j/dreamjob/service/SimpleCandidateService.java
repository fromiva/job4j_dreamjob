package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;
import ru.job4j.dreamjob.repository.CandidateRepository;

import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public final class SimpleCandidateService implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final FileService fileService;

    public SimpleCandidateService(CandidateRepository sql2oCandidateRepository,
                                  FileService fileService) {
        this.candidateRepository = sql2oCandidateRepository;
        this.fileService = fileService;
    }

    @Override
    public Candidate save(Candidate candidate, FileDto image) {
        saveNewFile(candidate, image);
        return candidateRepository.save(candidate);
    }

    private void saveNewFile(Candidate candidate, FileDto image) {
        File file = fileService.save(image);
        candidate.setFileId(file.getId());
    }

    @Override
    public boolean deleteById(int id) {
        Optional<Candidate> candidateOptional = findById(id);
        if (candidateOptional.isEmpty()) {
            return false;
        }
        boolean result = candidateRepository.deleteById(id);
        fileService.deleteById(candidateOptional.get().getFileId());
        return result;
    }

    @Override
    public boolean update(Candidate candidate, FileDto image) {
        if (image.getContent().length == 0) {
            return candidateRepository.update(candidate);
        }
        int oldFileId = candidate.getFileId();
        saveNewFile(candidate, image);
        boolean result = candidateRepository.update(candidate);
        fileService.deleteById(oldFileId);
        return result;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}
