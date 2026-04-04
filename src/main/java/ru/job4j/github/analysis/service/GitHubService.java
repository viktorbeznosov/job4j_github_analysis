package ru.job4j.github.analysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.job4j.github.analysis.dto.CommitResponseDto;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GitHubService {

    @Value("${github.username}")
    private String username;

    @Autowired
    private RestTemplate restTemplate;

    public List<Repository> fetchRepositories() {
        try {
            String url = "https://api.github.com/users/" + username + "/repos";
            ResponseEntity<List<Repository>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Repository>>() {}
            );
            return response.getBody();
        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * Получает список коммитов для указанного репозитория
     */
    public List<Commit> fetchCommits(String repository) {
        try {
            String url = "https://api.github.com/repos/" + username + "/" + repository + "/commits";
            log.info("Fetching commits from: {}", url);

            // Получаем DTO с полным ответом от GitHub
            ResponseEntity<List<CommitResponseDto>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<CommitResponseDto>>() {}
            );

            List<CommitResponseDto> commitDTOs = response.getBody();
            if (commitDTOs == null || commitDTOs.isEmpty()) {
                log.info("No commits found for repository: {}", repository);
                return new ArrayList<>();
            }

            // Конвертируем DTO в сущность Commit
            List<Commit> commits = new ArrayList<>();
            for (CommitResponseDto dto : commitDTOs) {
                if (dto.getCommit() != null && dto.getCommit().getAuthor() != null) {
                    Commit commit = new Commit();
                    commit.setMessage(dto.getCommit().getMessage());
                    commit.setAuthor(dto.getCommit().getAuthor().getName());
                    commit.setDate(dto.getCommit().getAuthor().getDate());
                    commit.setCommitHash(dto.getSha());
                    commits.add(commit);
                }
            }

            log.info("Fetched {} commits for repository: {}", commits.size(), repository);
            return commits;

        } catch (Exception e) {
            log.error("Error fetching commits for repository {}: {}", repository, e.getMessage(), e);
            return null;
        }
    }

}