package ru.job4j.github.analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.service.CommitService;
import ru.job4j.github.analysis.service.RepositoryService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GitHubController {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private CommitService commitService;

    @GetMapping("/repositories")
    public ResponseEntity<List<Repository>> getAllRepositories() {
        return ResponseEntity.ok(repositoryService.findAll());
    }

    @GetMapping("/commits/{name}")
    public ResponseEntity<List<Commit>> getCommits(@PathVariable(value = "name") String name) {
        return ResponseEntity.ok(commitService.findAllByRepositoryName(name));
    }

    @PostMapping("/repository")
    public ResponseEntity<Void> create(@RequestBody Repository repository) {
        repositoryService.create(repository);
        return ResponseEntity.noContent().build();
    }
}
