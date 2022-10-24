package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.PriceHistory;

import javax.persistence.OptimisticLockException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PriceHistoryRepository {

    private final MainRepository repository;
    private final static Logger LOGGER = LoggerFactory.getLogger(PriceHistoryRepository.class);

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
                LOGGER.debug("Optimistic Lock Exception", e);
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
