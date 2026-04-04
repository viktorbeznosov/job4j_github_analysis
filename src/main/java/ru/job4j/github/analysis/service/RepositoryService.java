package ru.job4j.github.analysis.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.RepositoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RepositoryService {
    private final RepositoryRepository repository;

    public RepositoryService(RepositoryRepository repository) {
        this.repository = repository;
    }

    @Async("saveRepositoryPool")
    public void create(Repository repository) {
        Optional<Repository> existing = findByName(repository.getName());
        if (existing.isEmpty()) {
            this.repository.save(repository);
        }
    }

    public Optional<Repository> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Repository> findByName(String name) {
        return repository.findByName(name);
    }

    public List<Repository> findAll() {
        return repository.findAll();
    }
}
