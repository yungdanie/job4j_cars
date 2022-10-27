package ru.job4j.cars.repository;


import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.User;

import javax.persistence.criteria.*;
import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@AllArgsConstructor
@Repository
public class UserRepository {
    private final MainRepository repository;

    public boolean checkAuth(User user) {
        return repository.
                getUniqResult("from User where login = :login",
                        Map.of("login", user.getLogin()),
                        User.class).
                isPresent();
    }

    public void mergeUser(User user) {
        repository.tx((Consumer<Session>) session -> session.merge(user));
    }

    public Optional<User> getUserByCookie(Cookie cookie) {
        if (!cookie.getName().equals("user_uuid")) {
            throw new IllegalArgumentException();
        }
        return repository.tx(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> userCriteriaQuery = cb.createQuery(User.class);
            Root<User> root = userCriteriaQuery.from(User.class);
            userCriteriaQuery.select(root);
            Path<Object> path = root.get("uuid");
            CriteriaBuilder.In<Object> in = cb.in(path);
            in.value(cookie.getValue());
            return session.createQuery(userCriteriaQuery).uniqueResultOptional();
        });
    }

    public User create(User user) {
        repository.tx((Consumer<Session>) session -> session.persist(user));
        return user;
    }

    public Optional<User> authentication(User user) {
        return repository.getUniqResult("from User u left join fetch u.uuids where login = :login and password = :password",
                Map.of("login", user.getLogin(), "password", user.getPassword()),
                User.class);
    }

    public void update(User user) {
        repository.tx((Consumer<Session>) session -> session.merge(user));
    }

    public void delete(User user) {
        repository.tx((Consumer<Session>) session -> session.delete(user));
    }

    public List<User> findAllOrderById() {
        return repository.getList("from User order by id", User.class);
    }

    public Optional<User> findById(int userId) {
        return repository.getUniqResult("from User where id = :userId", Map.of("userId", userId), User.class);
    }

    public List<User> findByLikeLogin(String key) {
        return repository.getList("from User where login like :key", Map.of("key", "%" + key.toLowerCase() + "%"), User.class);
    }

    public Optional<User> findByLogin(String key) {
        return repository.getUniqResult("from User where login like :key", Map.of("key", key), User.class);
    }
}
