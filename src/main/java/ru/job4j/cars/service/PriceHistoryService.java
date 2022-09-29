package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.PriceHistoryRepository;
import ru.job4j.cars.util.NotificationUtil;

@AllArgsConstructor
@Service
public class PriceHistoryService {

    private final PriceHistoryRepository store;

    public PriceHistory save(PriceHistory priceHistory) {
        store.save(priceHistory);
        if (priceHistory.getId() != 0) {
            NotificationUtil.notifyAllUsers(priceHistory.getPost());
        }
        return priceHistory;
    }

}
