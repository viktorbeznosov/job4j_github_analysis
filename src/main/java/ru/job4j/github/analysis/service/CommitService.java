package ru.job4j.github.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.CommitRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CommitService {

    private final CommitRepository commitRepository;

    public CommitService(CommitRepository commitRepository) {
        this.commitRepository = commitRepository;
    }

    public Optional<Commit> findById(Long id) {
        return commitRepository.findById(id);
    }

    public Optional<Commit> findByCommitHash(String commitHash) {
        return commitRepository.findByCommitHash(commitHash);
    }

    public List<Commit> findAllByRepositoryName(String repositoryName) {
        return commitRepository.findAllByRepositoryName(repositoryName);
    }

    public void save(Commit commit) {
        Optional<Commit> existing = findByCommitHash(commit.getCommitHash());
        if (existing.isEmpty()) {
            commitRepository.save(commit);
        }
    }

    @Async("saveCommitPool")
    public void saveMany(List<Commit> commits, Repository repository) {
        for (Commit commit: commits) {
            try {
                commit.setRepository(repository);
                save(commit);
            } catch (Exception e) {
                log.error("Error saving commit: {} {}", repository.getName(), commit.getMessage(), e);
            }
        }
    }
}
