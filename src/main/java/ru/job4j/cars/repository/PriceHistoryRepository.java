package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.PriceHistory;

import java.util.function.Consumer;

@Repository
@AllArgsConstructor
public class PriceHistoryRepository {

    private final MainRepository repository;

    public PriceHistory save(PriceHistory priceHistory) {
        repository.tx((Consumer<Session>) session -> session.save(priceHistory));
        return priceHistory;
    }
}
