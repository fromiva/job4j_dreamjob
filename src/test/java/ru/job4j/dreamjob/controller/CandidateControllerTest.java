package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateControllerTest {
    private CandidateService candidateService;
    private CityService cityService;
    private CandidateController candidateController;
    private MultipartFile file;

    @BeforeEach
    void initServices() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        file = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestCandidateListPageThenGetPageWithCandidates() {
        Candidate candidate1 = new Candidate(1, "test1", "desc1", now(), 1, 2);
        Candidate candidate2 = new Candidate(2, "test2", "desc2", now(), 3, 4);
        List<Candidate> expected = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expected);

        Model model = new ConcurrentModel();
        String view = candidateController.getAll(model);
        Object actual = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenRequestCandidateByIdThenGetPageWithCandidate() {
        int id = 1;
        Candidate expected = new Candidate(id, "test1", "desc1", now(), 1, 2);
        when(candidateService.findById(id)).thenReturn(Optional.of(expected));

        Model model = new ConcurrentModel();
        String view = candidateController.getById(model, id);
        Object actual = model.getAttribute("candidate");

        assertThat(view).isEqualTo("candidates/one");
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetPageWithCities() {
        City city1 = new City(1, "Москва");
        City city2 = new City(2, "Санкт-Петербург");
        List<City> expected = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expected);

        Model model = new ConcurrentModel();
        String view = candidateController.getCreationPage(model);
        Object actual = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenPostCandidateWithFileThenSameDataAndRedirectToCandidatesPage()
            throws IOException {
        Candidate expectedCandidate = new Candidate(1, "test1", "desc1", now(), 1, 2);
        FileDto expectedFileDto = new FileDto(file.getOriginalFilename(), file.getBytes());
        ArgumentCaptor<Candidate> candidateArgumentCaptor = ArgumentCaptor
                .forClass(Candidate.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor
                .forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(),
                fileDtoArgumentCaptor.capture())).thenReturn(expectedCandidate);

        Model model = new ConcurrentModel();
        String view = candidateController.create(expectedCandidate, file, model);
        Candidate actualCandidate = candidateArgumentCaptor.getValue();
        FileDto actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(expectedCandidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(expectedFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        RuntimeException expected = new RuntimeException("Failed to write file");
        when(candidateService.save(any(), any())).thenThrow(expected);

        Model model = new ConcurrentModel();
        String view = candidateController.create(new Candidate(), file, model);
        Object actual = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actual).isEqualTo(expected.getMessage());
    }
}
