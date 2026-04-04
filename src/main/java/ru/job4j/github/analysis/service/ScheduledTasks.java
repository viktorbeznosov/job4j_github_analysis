package ru.job4j.github.analysis.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class ScheduledTasks {
    private final CommitService commitService;
    private final RepositoryService repositoryService;
    private final GitHubService gitHubService;

//    @Bean("saveRepositoryPool")
    public ThreadPoolTaskExecutor initSaveRepositoryPool() {
        var pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(1);
        return pool;
    }

//    @Bean("saveCommitPool")
    public ThreadPoolTaskExecutor initSaveCommitPool() {
        var pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(1);
        return pool;
    }

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fetchRepositories() {
        try {
            List<Repository> repositories = gitHubService.fetchRepositories();
            if (repositories != null) {
                for (Repository repo : repositories) {
                    try {
                        repositoryService.create(repo);
                    } catch (Exception e) {
                        log.error("Error saving repository: {}", repo.getName(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error fetching repositories", e);
        }
    }

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void fetchCommits() {
        try {
            List<Repository> repositories = repositoryService.findAll();
            repositories.stream()
                .forEach(repository -> {
                    List<Commit> commits = gitHubService.fetchCommits(repository.getName());
                    if (commits != null) {
                        commitService.saveMany(commits, repository);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
        } catch (Exception e) {
            log.error("Error fetching commits", e);
        }
    }
}
