package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {
    private FileService fileService;
    private FileController fileController;

    @BeforeEach
    void setUp() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    void whenRequiestFileContentByIdThenGet() {
        int id = 1;
        FileDto fileDto = new FileDto("testFile.img", new byte[] {1, 2, 3});
        when(fileService.getFileById(id)).thenReturn(Optional.of(fileDto));

        ResponseEntity<?> response = fileController.getById(id);
        var actual = response.getBody();
        assertThat(actual).isEqualTo(fileDto.getContent());
    }
}
