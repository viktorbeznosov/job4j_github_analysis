package ru.job4j.github.analysis.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.github.analysis.model.Repository;
import ru.job4j.github.analysis.repository.RepositoryRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepositoryServiceTest {

    @Mock
    private RepositoryRepository repositoryRepository;

    @InjectMocks
    private RepositoryService repositoryService;

    private Repository testRepository;

    @BeforeEach
    void setUp() {
        testRepository = new Repository();
        testRepository.setId(1L);
        testRepository.setName("test-repo");
        testRepository.setUrl("https://github.com/test/test-repo");
    }

    @Test
    void createWhenRepositoryDoesNotExistShouldSave() {
        when(repositoryRepository.findByName(testRepository.getName()))
                .thenReturn(Optional.empty());

        repositoryService.create(testRepository);

        ArgumentCaptor<Repository> captor = ArgumentCaptor.forClass(Repository.class);
        verify(repositoryRepository, times(1)).save(captor.capture());

        Repository savedRepository = captor.getValue();
        assertThat(savedRepository.getName()).isEqualTo(testRepository.getName());
        assertThat(savedRepository.getUrl()).isEqualTo(testRepository.getUrl());
    }

    @Test
    void createWhenRepositoryAlreadyExistsShouldNotSave() {
        when(repositoryRepository.findByName(testRepository.getName()))
                .thenReturn(Optional.of(testRepository));

        repositoryService.create(testRepository);

        verify(repositoryRepository, never()).save(any(Repository.class));
    }

    @Test
    void findByIdWhenRepositoryExistsShouldReturnRepository() {
        when(repositoryRepository.findById(1L)).thenReturn(Optional.of(testRepository));

        Optional<Repository> result = repositoryService.findById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getName()).isEqualTo("test-repo");
    }

    @Test
    void findByIdWhenRepositoryDoesNotExistShouldReturnEmpty() {
        when(repositoryRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Repository> result = repositoryService.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByNameWhenRepositoryExistsShouldReturnRepository() {
        when(repositoryRepository.findByName("test-repo")).thenReturn(Optional.of(testRepository));

        Optional<Repository> result = repositoryService.findByName("test-repo");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("test-repo");
    }

    @Test
    void findByNameWhenRepositoryDoesNotExistShouldReturnEmpty() {
        when(repositoryRepository.findByName("non-existent")).thenReturn(Optional.empty());

        Optional<Repository> result = repositoryService.findByName("non-existent");

        assertThat(result).isEmpty();
    }

    @Test
    void findAllShouldReturnAllRepositories() {
        Repository secondRepository = new Repository();
        secondRepository.setId(2L);
        secondRepository.setName("second-repo");
        secondRepository.setUrl("https://github.com/test/second-repo");

        List<Repository> repositories = List.of(testRepository, secondRepository);
        when(repositoryRepository.findAll()).thenReturn(repositories);

        List<Repository> result = repositoryService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(testRepository, secondRepository);
        verify(repositoryRepository, times(1)).findAll();
    }

    @Test
    void findAllWhenNoRepositoriesShouldReturnEmptyList() {
        when(repositoryRepository.findAll()).thenReturn(List.of());

        List<Repository> result = repositoryService.findAll();

        assertThat(result).isEmpty();
        verify(repositoryRepository, times(1)).findAll();
    }

    @Test
    void createWithNullValuesShouldStillValidate() {
        Repository emptyRepository = new Repository();

        when(repositoryRepository.findByName(null)).thenReturn(Optional.empty());
        repositoryService.create(emptyRepository);

        verify(repositoryRepository, times(1)).save(emptyRepository);
    }

    @Test
    void createShouldBeAsync() throws Exception {
        // Проверяем наличие аннотации @Async
        var method = RepositoryService.class.getDeclaredMethod("create", Repository.class);
        assertThat(method.isAnnotationPresent(org.springframework.scheduling.annotation.Async.class)).isTrue();
        assertThat(method.getAnnotation(org.springframework.scheduling.annotation.Async.class).value())
                .isEqualTo("saveRepositoryPool");
    }
}