package ru.job4j.github.analysis.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.job4j.github.analysis.model.Commit;

import java.util.List;
import java.util.Optional;

public interface CommitRepository extends CrudRepository<Commit, Long> {
    public Optional<Commit> findById(Long id);

    public Optional<Commit> findByCommitHash(String commitHash);

    @Query("""
        SELECT c FROM Commit c
        JOIN Repository r ON r.id = c.repository.id
        WHERE r.name = :repositoryName
    """)
    public List<Commit> findAllByRepositoryName(@Param("repositoryName") String repositoryName);

}
