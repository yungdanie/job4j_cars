package ru.job4j.cars.repository;


import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.User;
import ru.job4j.cars.model.UuidEntity;

import javax.persistence.criteria.*;
import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
            throw new IllegalArgumentException("Cookie that used to get User has wrong name");
        }
        return repository.tx(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<User> uuidCriteriaQuery = cb.createQuery(User.class);
            Root<User> userRoot = uuidCriteriaQuery.from(User.class);
            Subquery<Integer> subQuery = uuidCriteriaQuery.subquery(Integer.class);
            Root<User> subUserRoot = subQuery.from(User.class);
            Join<User, UuidEntity> subQueryJoin = subUserRoot.join("uuids");
            subQuery.select(subUserRoot.get("id")).where(
                    cb.equal(subQueryJoin.get("uuid"), UUID.fromString(cookie.getValue()))
            );
            uuidCriteriaQuery.where(cb.in(userRoot.get("id")).value(subQuery));
            return session.createQuery(uuidCriteriaQuery).uniqueResultOptional();
        });
    }

    public User create(User user) {
        repository.tx((Consumer<Session>) session -> session.persist(user));
        return user;
    }

    public Optional<User> authentication(User user) {
        if (user == null || user.getLogin() == null || user.getPassword() == null) {
            return Optional.empty();
        }
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
