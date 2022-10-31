package ru.job4j.cars.control;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.service.PriceHistoryService;
import ru.job4j.cars.util.AuthUserUtil;

import javax.servlet.http.HttpSession;

@Controller
@AllArgsConstructor
public class PriceHistoryControl {

    private final PriceHistoryService priceHistoryService;

    @GetMapping("/calculatePriceHistory/{id}")
    public String calculatePriceHistory(@PathVariable int id) {
        return "";
    }

    @PostMapping("/createPriceHistory")
    public String createPriceHistory(@ModelAttribute PriceHistory priceHistory, @ModelAttribute Post post, Model model, HttpSession session) {
        priceHistory.setPost(post);
        priceHistoryService.save(priceHistory);
        AuthUserUtil.addUserToModel(session, model);
        return "redirect:/postPage/" + post.getId();
    }
}
