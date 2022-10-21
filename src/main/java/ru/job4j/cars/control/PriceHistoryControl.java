package ru.job4j.cars.control;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.job4j.cars.service.PriceHistoryService;

@Controller
@AllArgsConstructor
public class PriceHistoryControl {

    private final PriceHistoryService priceHistoryService;

    @GetMapping("/calculatePriceHistory/{id}")
    public String calculatePriceHistory(@PathVariable int id) {
        StringBuffer calculatedString = ;
    }
}
