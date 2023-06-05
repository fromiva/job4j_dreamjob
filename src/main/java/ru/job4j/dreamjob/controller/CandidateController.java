package ru.job4j.dreamjob.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;
import ru.job4j.dreamjob.repository.MemoryCandidateRepository;

import java.util.Optional;

@Controller
@RequestMapping("/candidates")
public class CandidateController {
    private final CandidateRepository repository = MemoryCandidateRepository.getInstance();

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("candidates", repository.findAll());
        return "candidates/list";
    }

    @GetMapping("/{id}")
    public String getById(Model model, @PathVariable int id) {
        Optional<Candidate> candidateOptional = repository.findById(id);
        if (candidateOptional.isEmpty()) {
            model.addAttribute("message",
                    "Кандидат с указанным идентификатором не найден");
            return "errors/404";
        }
        model.addAttribute("candidate", candidateOptional.get());
        return "candidates/one";
    }

    @GetMapping("/create")
    public String getCreationPage() {
        return "candidates/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Candidate candidate) {
        repository.save(candidate);
        return "redirect:/candidates";
    }

    @PostMapping("/update")
    public String update(Model model, @ModelAttribute Candidate candidate) {
        boolean success = repository.update(candidate);
        if (!success) {
            model.addAttribute("message",
                    "Кандидат с указанным идентификатором не найден");
            return "errors/404";
        }
        return "redirect:/candidates";
    }

    @GetMapping("/delete/{id}")
    public String delete(Model model, @PathVariable int id) {
        boolean success = repository.deleteById(id);
        if (!success) {
            model.addAttribute("message",
                    "Кандидат с указанным идентификатором не найден");
            return "errors/404";
        }
        return "redirect:/candidates";
    }
}
