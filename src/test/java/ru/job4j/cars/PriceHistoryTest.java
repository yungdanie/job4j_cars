package ru.job4j.cars;

import org.assertj.core.api.Assertions;
import org.hibernate.Session;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.MainRepository;
import ru.job4j.cars.repository.PostRepository;
import ru.job4j.cars.repository.PriceHistoryRepository;

import java.util.function.Function;

public class PriceHistoryTest {

    private final MainRepository mainRepository = new MainRepository(new MetadataSources(new StandardServiceRegistryBuilder()
            .configure().build())
            .buildMetadata().buildSessionFactory());

    private final PriceHistoryRepository priceHistoryRepository = new PriceHistoryRepository(mainRepository);

    private final PostRepository postRepository = new PostRepository(mainRepository);

    @BeforeEach
    @AfterEach
    public void clearDataBase() {
        mainRepository.tx((Function<Session, Object>) session -> session.createQuery("delete from PriceHistory").executeUpdate());
        mainRepository.tx((Function<Session, Object>) session -> session.createQuery("delete from Post").executeUpdate());
    }

    @Test
    public void whenGetLastPrice() {
        PriceHistory oldPrice = new PriceHistory();
        PriceHistory newPrice = new PriceHistory();
        oldPrice.setPrice(19000);
        newPrice.setPrice(20000);
        Post post = new Post();
        post.setText("test");
        postRepository.save(post);
        oldPrice.setPost(post);
        newPrice.setPost(post);
        priceHistoryRepository.saveAndSetLast(oldPrice);
        priceHistoryRepository.saveAndSetLast(newPrice);
        Assertions.assertThat(priceHistoryRepository.getLastPriceByPostId(post.getId()).get()).isEqualTo(newPrice);
    }

}
