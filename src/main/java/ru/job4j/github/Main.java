package ru.job4j.github;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.service.CommitService;
import ru.job4j.github.analysis.service.GitHubService;
import ru.job4j.github.analysis.service.RepositoryService;
import ru.job4j.github.analysis.service.ScheduledTasks;

import java.util.List;
import java.util.Optional;

@EnableScheduling
@SpringBootApplication
@AllArgsConstructor
public class Main implements CommandLineRunner {

    private final GitHubService gitHubService;

    private final ScheduledTasks scheduledTasks;

    private final CommitService commitService;

    private final RepositoryService repositoryService;

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================== START ==========================================");
    }
}