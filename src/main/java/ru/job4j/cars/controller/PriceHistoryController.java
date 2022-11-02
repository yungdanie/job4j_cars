package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.service.PriceHistoryService;
import ru.job4j.cars.util.AuthUserUtil;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
public class PriceHistoryController {

    private final PriceHistoryService priceHistoryService;

    @PostMapping("/createPriceHistory")
    public String createPriceHistory(@ModelAttribute PriceHistory priceHistory, @ModelAttribute Post post, Model model, HttpSession session) {
        priceHistory.setPost(post);
        priceHistoryService.save(priceHistory);
        AuthUserUtil.addUserToModel(session, model);
        return "redirect:post/postPage/" + post.getId();
    }
}
