package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.repository.MemoryVacancyRepository;
import ru.job4j.dreamjob.repository.VacancyRepository;

import java.util.Optional;

@Controller
@RequestMapping("/vacancies")
public class VacancyController {
    private final VacancyRepository repository = MemoryVacancyRepository.getInstance();

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("vacancies", repository.findAll());
        return "vacancies/list";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        Optional<Vacancy> vacancyOptional = repository.findById(id);
        if (vacancyOptional.isEmpty()) {
            model.addAttribute("message",
                    "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        model.addAttribute("vacancy", vacancyOptional.get());
        return "vacancies/one";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "vacancies/create";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute Vacancy vacancy) {
        boolean success = repository.update(vacancy);
        if (!success) {
            model.addAttribute("message",
                    "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean success = repository.deleteById(id);
        if (!success) {
            model.addAttribute("message",
                    "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Vacancy vacancy) {
        repository.save(vacancy);
        return "redirect:/vacancies";
    }
}
