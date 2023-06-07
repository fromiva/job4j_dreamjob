package ru.job4j.dreamjob.controller;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.VacancyService;

import java.util.Optional;

@ThreadSafe
@Controller
@RequestMapping("/vacancies")
public final class VacancyController {
    private final VacancyService vacancyService;
    private final CityService cityService;

    public VacancyController(VacancyService vacancyService, CityService cityService) {
        this.vacancyService = vacancyService;
        this.cityService = cityService;
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("vacancies", vacancyService.findAll());
        return "vacancies/list";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        Optional<Vacancy> vacancyOptional = vacancyService.findById(id);
        if (vacancyOptional.isEmpty()) {
            model.addAttribute("message",
                    "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        model.addAttribute("cities", cityService.findAll());
        model.addAttribute("vacancy", vacancyOptional.get());
        return "vacancies/one";
    }

    @GetMapping("/create")
    public String getCreationPage(Model model) {
        model.addAttribute("cities", cityService.findAll());
        return "vacancies/create";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute Vacancy vacancy) {
        boolean success = vacancyService.update(vacancy);
        if (!success) {
            model.addAttribute("message",
                    "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean success = vacancyService.deleteById(id);
        if (!success) {
            model.addAttribute("message",
                    "Вакансия с указанным идентификатором не найдена");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Vacancy vacancy) {
        vacancyService.save(vacancy);
        return "redirect:/vacancies";
    }
}
