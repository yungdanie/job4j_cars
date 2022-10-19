package ru.job4j.cars.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class index {

    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
