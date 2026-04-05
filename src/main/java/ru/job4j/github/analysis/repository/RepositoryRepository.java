package ru.job4j.github.analysis.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.github.analysis.model.Repository;

import java.util.List;
import java.util.Optional;

public interface RepositoryRepository extends CrudRepository<Repository, Long> {
    Optional<Repository> findById(Long id);

    Optional<Repository> findByName(String name);

    List<Repository> findAll();
}
