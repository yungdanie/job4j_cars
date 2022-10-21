package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.PriceHistory;

import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class PriceHistoryRepository {

    private final MainRepository repository;

    public List<PriceHistory> getById(int postId) {
        return repository
                .getList("from PriceHistory where post_id = :id",
                        Map.of("id", postId),
                        PriceHistory.class);
    }
}
