package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
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
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Post> criteriaQuery = builder.createQuery(Post.class);
            Root<Post> root = criteriaQuery.from(Post.class);
            return session.createQuery(criteriaQuery.select(root).
                    where(builder.equal(criteriaQuery.subquery(Post.class).
                            where(builder.greatest(root.get("created"))), root.get("created")))).list();
        });
    }
}
