package ru.job4j.cars;

import org.assertj.core.api.Assertions;
import org.hibernate.Session;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.MainRepository;
import ru.job4j.cars.repository.PostRepository;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class HibernateUsageTest {

    private final MainRepository mainRepository = new MainRepository(new MetadataSources(new StandardServiceRegistryBuilder()
            .configure().build())
            .buildMetadata().buildSessionFactory());

    private final PostRepository postRepository = new PostRepository(mainRepository);

    @BeforeEach
    public void clearDataBase() {
        mainRepository.tx((Function<Session, Object>) session -> session.createQuery("delete from Post").executeUpdate());
        mainRepository.tx((Function<Session, Object>) session -> session.createQuery("delete from User").executeUpdate());
    }

    @Test
    public void truncDate() {
        Post lastPost = new Post();
        Post anotherLastPost = new Post();
        Post oldPost = new Post();
        Post anotherOldPost = new Post();
        LocalDateTime dateTime = LocalDateTime.of(2022, 10, 10, 10, 10);
        lastPost.setText("testing1");
        lastPost.setCreated(dateTime);
        oldPost.setText("testing2");
        oldPost.setCreated(dateTime.minusDays(1));
        anotherOldPost.setText("testing3");
        oldPost.setCreated(dateTime.minusMonths(1));
        anotherLastPost.setText("testing4");
        anotherLastPost.setCreated(dateTime.minusHours(1));
        postRepository.save(lastPost);
        postRepository.save(oldPost);
        postRepository.save(anotherOldPost);
        postRepository.save(anotherLastPost);
        List<Post> list = mainRepository.tx(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Post> listQuery = cb.createQuery(Post.class);
            Root<Post> root = listQuery.from(Post.class);
            listQuery.select(root);
            Subquery<LocalDate> sub = listQuery.subquery(LocalDate.class);
            Path<LocalDate> selection = sub.from(Post.class).get("created");
            sub.select(cb.greatest(selection).as(LocalDate.class));
            listQuery.where(cb.equal(sub, root.get("created").as(LocalDate.class)));
            return session.createQuery(listQuery).list();
        });
        Assertions.assertThat(list).isEqualTo(List.of(lastPost, anotherLastPost));
    }

    @Test
    public void customArrayTypeTest() {
        User user = new User();
        user.setLogin("login");
        user.setPassword("password");
        UUID firstID = UUID.randomUUID();
        UUID secondID = UUID.randomUUID();
        user.setUuid(new UUID[] {firstID, secondID});
        mainRepository.tx((Consumer<Session>) session -> session.persist(user));
        Assertions.assertThat(user.getUuid()).isEqualTo(new UUID[] {firstID, secondID});
    }
}
