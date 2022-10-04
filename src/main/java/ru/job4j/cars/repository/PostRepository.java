package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@AllArgsConstructor
@Repository
public class PostRepository {

    private final MainRepository repository;

    public Post save(Post post) {
        repository.tx((Consumer<Session>) session -> session.persist(post));
        return post;
    }

    public List<Post> getLastDay() {
        return repository.getList("from Post where created = (select max(created) from Post)", Post.class);
    }

    public List<Post> like(String key) {
        return repository.getList("from Post where text like %:key%", Map.of("key", key.toLowerCase()), Post.class);
    }
}
