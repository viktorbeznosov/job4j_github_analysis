package ru.job4j.github.analysis.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;

import java.util.List;
import java.util.Optional;

public interface RepositoryRepository extends CrudRepository<Repository, Long> {
    public Optional<Repository> findById(Long id);

    public Optional<Repository> findByName(String name);

    public List<Repository> findAll();
}
