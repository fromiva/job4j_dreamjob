package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {
    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();
    private final Map<Integer, Candidate> candidates = new HashMap<>();
    private int nextId = 1;

    private MemoryCandidateRepository() {
        save(new Candidate(0,
                "Kirilov Viktor Sergeevich",
                "Middle Java Developer with 3 years of experience",
                LocalDateTime.now().minusDays(22)));
        save(new Candidate(0,
                "Mishina Olga Konstantinovna",
                "Senior Java Developer with 6+ years of experience",
                LocalDateTime.now().minusDays(17)));
        save(new Candidate(0,
                "Vasilev Vladislav Semenovich",
                "Junior+ Java Developer with 1.5 years of experience",
                LocalDateTime.now().minusDays(2)));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(
                        oldCandidate.getId(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate()
                )) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
