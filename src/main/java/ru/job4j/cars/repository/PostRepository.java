package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import java.util.function.Consumer;

@AllArgsConstructor
@Repository
public class PostRepository {

    private final MainRepository repository;

    public Post save(Post post) {
        repository.tx((Consumer<Session>) session -> session.persist(post));
        return post;
    }
}
