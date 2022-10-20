package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        return repository.tx(session -> {
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
    }

    public List<Post> getByTextContains(String key) {
        return repository.getList("from Post where text like :key", Map.of("key", "%" + key.toLowerCase() + "%"), Post.class);
    }

    public Optional<Post> getById(int id) {
        return repository.getUniqResult("from Post where id = :id", Map.of("id", id), Post.class);
    }
}
