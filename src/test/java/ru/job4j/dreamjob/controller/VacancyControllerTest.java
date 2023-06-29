package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class VacancyControllerTest {
    private VacancyService vacancyService;
    private CityService cityService;
    private VacancyController vacancyController;
    private MultipartFile file;

    @BeforeEach
    void initServices() {
        vacancyService = mock(VacancyService.class);
        cityService = mock(CityService.class);
        vacancyController = new VacancyController(vacancyService, cityService);
        file = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenRequestVacancyListPageThenGetPageWithVacancies() {
        Vacancy vacancy1 = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        Vacancy vacancy2 = new Vacancy(2, "test2", "desc2", now(), false, 3, 4);
        List<Vacancy> expected = List.of(vacancy1, vacancy2);
        when(vacancyService.findAll()).thenReturn(expected);

        Model model = new ConcurrentModel();
        String view = vacancyController.getAll(model);
        Object actual = model.getAttribute("vacancies");

        assertThat(view).isEqualTo("vacancies/list");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenRequestVacancyByIdThenGetPageWithVacancy() {
        int id = 1;
        Vacancy expected = new Vacancy(id, "test1", "desc1", now(), true, 1, 2);
        when(vacancyService.findById(id)).thenReturn(Optional.of(expected));

        Model model = new ConcurrentModel();
        String view = vacancyController.getById(model, id);
        Object actual = model.getAttribute("vacancy");

        assertThat(view).isEqualTo("vacancies/one");
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);

    }

    @Test
    public void whenRequestVacancyCreationPageThenGetPageWithCities() {
        City city1 = new City(1, "Москва");
        City city2 = new City(2, "Санкт-Петербург");
        List<City> expected = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expected);

        Model model = new ConcurrentModel();
        String view = vacancyController.getCreationPage(model);
        Object actual = model.getAttribute("cities");

        assertThat(view).isEqualTo("vacancies/create");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void whenPostVacancyWithFileThenSameDataAndRedirectToVacanciesPage() throws IOException {
        Vacancy expectedVacancy = new Vacancy(1, "test1", "desc1", now(), true, 1, 2);
        FileDto expectedFileDto = new FileDto(file.getOriginalFilename(), file.getBytes());
        ArgumentCaptor<Vacancy> vacancyArgumentCaptor = ArgumentCaptor.forClass(Vacancy.class);
        ArgumentCaptor<FileDto> fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(vacancyService.save(vacancyArgumentCaptor.capture(), fileDtoArgumentCaptor.capture()))
                .thenReturn(expectedVacancy);

        Model model = new ConcurrentModel();
        String view = vacancyController.create(expectedVacancy, file, model);
        Vacancy actualVacancy = vacancyArgumentCaptor.getValue();
        FileDto actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualVacancy).isEqualTo(expectedVacancy);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(expectedFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        RuntimeException expected = new RuntimeException("Failed to write file");
        when(vacancyService.save(any(), any())).thenThrow(expected);

        Model model = new ConcurrentModel();
        String view = vacancyController.create(new Vacancy(), file, model);
        Object actual = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actual).isEqualTo(expected.getMessage());
    }
}
