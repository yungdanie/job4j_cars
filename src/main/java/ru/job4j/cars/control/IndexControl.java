package ru.job4j.cars.control;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.cars.repository.PostRepository;
import ru.job4j.cars.service.PostService;

@Controller
@AllArgsConstructor
public class IndexControl {

    private final PostService postService;

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("postList", postService.getAll());
        return "index";
    }
}
