package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;

import javax.persistence.OptimisticLockException;
import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Repository
@AllArgsConstructor
public class PriceHistoryRepository {

    private final MainRepository repository;
    private final Logger logger;

    public PriceHistory save(PriceHistory priceHistory) {
        priceHistory.setLast(true);
        repository.tx(session -> {
            try {
                CriteriaBuilder cb = session.getCriteriaBuilder();
                CriteriaQuery<PriceHistory> criteriaQuery = cb.createQuery(PriceHistory.class);
                Root<PriceHistory> root = criteriaQuery.from(PriceHistory.class);
                criteriaQuery.select(root).where(cb.and(cb.equal(root.get("post").get("id"), priceHistory.getPost().getId())),
                        cb.equal(root.get("isLast"), true));
                session.createQuery(criteriaQuery).getSingleResult().setLast(false);
                session.persist(priceHistory);
            } catch (OptimisticLockException e) {
                session.close();
                logger.debug("Optimistic Lock Exception", e);
                priceHistory.setLast(false);
            }
        });
        return priceHistory;
    }
    public Optional<PriceHistory> getLastPriceByPostId(int postId) {
        return repository.tx(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<PriceHistory> priceQuery = cb.createQuery(PriceHistory.class);
            Root<PriceHistory> root = priceQuery.from(PriceHistory.class);
            priceQuery.select(root);
            priceQuery.where(cb.equal(root.get("isLast"), true));
            return session.createQuery(priceQuery).uniqueResultOptional();
        });
    }
}
