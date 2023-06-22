package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.File;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryFileRepository implements FileRepository {
    private final AtomicInteger nextId = new AtomicInteger(1);
    private final Map<Integer, File> files = new ConcurrentHashMap<>();

    @Override
    public File save(File file) {
        int id = nextId.getAndIncrement();
        file.setId(id);
        files.put(id, file);
        return file;
    }

    @Override
    public Optional<File> findById(int id) {
        return Optional.ofNullable(files.get(id));
    }

    @Override
    public boolean deleteById(int id) {
        return files.remove(id) != null;
    }
}
