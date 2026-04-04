package ru.job4j.github.analysis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commits")
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commit_hash", length = 40)
    private String commitHash;

    private String message;
    private String author;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

    // Конструктор для создания из DTO
    public Commit() {}

    public Commit(String message, String author, LocalDateTime date) {
        this.message = message;
        this.author = author;
        this.date = date;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("sha")
    public void setCommitHash(String sha) {
        this.commitHash = sha;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}