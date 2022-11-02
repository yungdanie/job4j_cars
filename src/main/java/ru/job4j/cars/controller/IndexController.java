package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.job4j.cars.exception.UndefinedUserException;
import ru.job4j.cars.service.PostService;
import ru.job4j.cars.util.AuthUserUtil;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
public class IndexController {

    private final PostService postService;

    @GetMapping("/index")
    public String index(Model model, HttpSession session) {
        model.addAttribute("postList", postService.getAllFetchPriceHAndParticipates());
        AuthUserUtil.addUserToModel(session, model);
        return "index";
    }
}
