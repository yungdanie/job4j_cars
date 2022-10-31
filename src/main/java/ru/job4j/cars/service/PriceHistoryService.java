package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.PriceHistoryRepository;

import java.util.Optional;

@AllArgsConstructor
@Service
public class PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    public Optional<PriceHistory> getLastPrice(int postId) {
        return priceHistoryRepository.getLastPriceByPostId(postId);
    }

    public PriceHistory save(PriceHistory priceHistory) {
        return priceHistoryRepository.save(priceHistory);
    }
}
