package ru.job4j.github.analysis.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.github.analysis.model.Commit;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.CommitRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommitServiceTest {
    @Mock
    private CommitRepository commitRepository;

    @InjectMocks
    private CommitService commitService;

    private Commit testCommit;
    private Repository testRepository;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        testRepository = new Repository();
        testRepository.setId(1L);
        testRepository.setName("test-repo");
        testRepository.setUrl("https://github.com/test/test-repo");

        testCommit = new Commit();
        testCommit.setId(1L);
        testCommit.setCommitHash("abc123def456");
        testCommit.setMessage("Initial commit");
        testCommit.setAuthor("John Doe");
        testCommit.setDate(now);
        testCommit.setRepository(testRepository);
    }

    @Test
    void findById_WhenCommitExists_ShouldReturnCommit() {
        when(commitRepository.findById(1L)).thenReturn(Optional.of(testCommit));

        Optional<Commit> result = commitService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getCommitHash()).isEqualTo("abc123def456");
        assertThat(result.get().getMessage()).isEqualTo("Initial commit");
    }

    @Test
    void findById_WhenCommitDoesNotExist_ShouldReturnEmpty() {
        when(commitRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Commit> result = commitService.findById(999L);

        assertThat(result).isEmpty();
        verify(commitRepository, times(1)).findById(999L);
    }

    @Test
    void findByCommitHash_WhenCommitExists_ShouldReturnCommit() {
        when(commitRepository.findByCommitHash("abc123def456"))
                .thenReturn(Optional.of(testCommit));

        Optional<Commit> result = commitService.findByCommitHash("abc123def456");

        assertThat(result).isPresent();
        assertThat(result.get().getCommitHash()).isEqualTo("abc123def456");
    }

    @Test
    void findByCommitHash_WhenCommitDoesNotExist_ShouldReturnEmpty() {
        when(commitRepository.findByCommitHash("nonexistent"))
                .thenReturn(Optional.empty());

        Optional<Commit> result = commitService.findByCommitHash("nonexistent");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByRepositoryName_ShouldReturnCommits() {
        Commit commit2 = new Commit();
        commit2.setId(2L);
        commit2.setCommitHash("xyz789");
        commit2.setMessage("Second commit");
        commit2.setAuthor("Jane Doe");
        commit2.setDate(now);

        List<Commit> commits = List.of(testCommit, commit2);
        when(commitRepository.findAllByRepositoryName("test-repo")).thenReturn(commits);

        List<Commit> result = commitService.findAllByRepositoryName("test-repo");

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testCommit, commit2);
        verify(commitRepository, times(1)).findAllByRepositoryName("test-repo");
    }

    @Test
    void findAllByRepositoryName_WhenNoCommits_ShouldReturnEmptyList() {
        when(commitRepository.findAllByRepositoryName("empty-repo"))
                .thenReturn(List.of());

        List<Commit> result = commitService.findAllByRepositoryName("empty-repo");

        assertThat(result).isEmpty();
    }

    @Test
    void save_WhenCommitDoesNotExist_ShouldSave() {
        when(commitRepository.findByCommitHash(testCommit.getCommitHash()))
                .thenReturn(Optional.empty());

        commitService.save(testCommit);

        ArgumentCaptor<Commit> captor = ArgumentCaptor.forClass(Commit.class);
        verify(commitRepository, times(1)).save(captor.capture());

        Commit savedCommit = captor.getValue();
        assertThat(savedCommit.getCommitHash()).isEqualTo(testCommit.getCommitHash());
        assertThat(savedCommit.getMessage()).isEqualTo(testCommit.getMessage());
    }

    @Test
    void save_WhenCommitAlreadyExists_ShouldNotSave() {
        when(commitRepository.findByCommitHash(testCommit.getCommitHash()))
                .thenReturn(Optional.of(testCommit));

        commitService.save(testCommit);

        verify(commitRepository, never()).save(any(Commit.class));
    }

    @Test
    void saveMany_ShouldSaveAllCommits() {
        Commit commit1 = new Commit();
        commit1.setCommitHash("hash1");
        commit1.setMessage("Commit 1");
        commit1.setAuthor("Author 1");
        commit1.setDate(now);

        Commit commit2 = new Commit();
        commit2.setCommitHash("hash2");
        commit2.setMessage("Commit 2");
        commit2.setAuthor("Author 2");
        commit2.setDate(now);

        List<Commit> commits = List.of(commit1, commit2);

        when(commitRepository.findByCommitHash(anyString())).thenReturn(Optional.empty());
        commitService.saveMany(commits, testRepository);
        ArgumentCaptor<Commit> captor = ArgumentCaptor.forClass(Commit.class);
        verify(commitRepository, times(2)).save(captor.capture());

        List<Commit> savedCommits = captor.getAllValues();
        assertThat(savedCommits).hasSize(2);

        for (Commit saved : savedCommits) {
            assertThat(saved.getRepository()).isEqualTo(testRepository);
            assertThat(saved.getCommitHash()).isIn("hash1", "hash2");
        }
    }

    @Test
    void saveMany_WhenSomeCommitsAlreadyExist_ShouldSaveOnlyNewOnes() {
        Commit newCommit = new Commit();
        newCommit.setCommitHash("new-hash");
        newCommit.setMessage("New commit");
        newCommit.setAuthor("New Author");
        newCommit.setDate(now);

        Commit existingCommit = new Commit();
        existingCommit.setCommitHash("existing-hash");
        existingCommit.setMessage("Existing commit");
        existingCommit.setAuthor("Existing Author");
        existingCommit.setDate(now);

        List<Commit> commits = List.of(newCommit, existingCommit);

        when(commitRepository.findByCommitHash("new-hash")).thenReturn(Optional.empty());
        when(commitRepository.findByCommitHash("existing-hash"))
                .thenReturn(Optional.of(existingCommit));

        commitService.saveMany(commits, testRepository);
        ArgumentCaptor<Commit> captor = ArgumentCaptor.forClass(Commit.class);
        verify(commitRepository, times(1)).save(captor.capture());

        Commit savedCommit = captor.getValue();
        assertThat(savedCommit.getCommitHash()).isEqualTo("new-hash");
        assertThat(savedCommit.getRepository()).isEqualTo(testRepository);
    }

    @Test
    void saveMany_WhenEmptyList_ShouldNotSaveAnything() {
        List<Commit> emptyCommits = List.of();
        commitService.saveMany(emptyCommits, testRepository);

        verify(commitRepository, never()).save(any(Commit.class));
    }

    @Test
    void saveMany_ShouldSetRepositoryForEachCommit() {
        Commit commit = new Commit();
        commit.setCommitHash("hash");
        commit.setMessage("Message");

        List<Commit> commits = List.of(commit);

        when(commitRepository.findByCommitHash("hash")).thenReturn(Optional.empty());
        commitService.saveMany(commits, testRepository);
        ArgumentCaptor<Commit> captor = ArgumentCaptor.forClass(Commit.class);
        verify(commitRepository, times(1)).save(captor.capture());

        Commit savedCommit = captor.getValue();
        assertThat(savedCommit.getRepository()).isSameAs(testRepository);
        assertThat(savedCommit.getRepository().getId()).isEqualTo(1L);
        assertThat(savedCommit.getRepository().getName()).isEqualTo("test-repo");
    }

    @Test
    void saveMany_WhenExceptionOccurs_ShouldLogErrorAndContinue() {
        Commit commit1 = new Commit();
        commit1.setCommitHash("hash1");
        commit1.setMessage("Commit 1");

        Commit commit2 = new Commit();
        commit2.setCommitHash("hash2");
        commit2.setMessage("Commit 2");

        List<Commit> commits = List.of(commit1, commit2);

        when(commitRepository.findByCommitHash("hash1"))
                .thenThrow(new RuntimeException("Database error"));
        when(commitRepository.findByCommitHash("hash2")).thenReturn(Optional.empty());

        commitService.saveMany(commits, testRepository);

        verify(commitRepository, never()).save(commit1);
        verify(commitRepository, times(1)).save(commit2);
    }

    @Test
    void save_WithNullCommitHash_ShouldSave() {
        Commit commitWithoutHash = new Commit();
        commitWithoutHash.setCommitHash(null);
        commitWithoutHash.setMessage("Message");

        when(commitRepository.findByCommitHash(null)).thenReturn(Optional.empty());
        commitService.save(commitWithoutHash);

        verify(commitRepository, times(1)).save(commitWithoutHash);
    }

    @Test
    void saveMany_ShouldBeAsync() throws Exception {
        var method = CommitService.class.getDeclaredMethod("saveMany", List.class, Repository.class);
        assertThat(method.isAnnotationPresent(org.springframework.scheduling.annotation.Async.class)).isTrue();
        assertThat(method.getAnnotation(org.springframework.scheduling.annotation.Async.class).value())
                .isEqualTo("saveCommitPool");
    }
}