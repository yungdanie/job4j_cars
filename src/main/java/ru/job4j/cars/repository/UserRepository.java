package ru.job4j.cars.repository;


import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import ru.job4j.cars.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UserRepository {
    private final SessionFactory sf;

    public User create(User user) {
        try (Session session = sf.openSession()) {
            try {
                session.beginTransaction();
                session.save(user);
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
            }
        }
        return user;
    }

    public void update(User user) {
        try (Session session = sf.openSession()) {
            try {
                session.beginTransaction();
                session.createQuery("update User set login = :fLogin, password = :fPassword where id = :fId")
                        .setParameter("fLogin", user.getLogin())
                        .setParameter("fPassword", user.getPassword())
                        .setParameter("fId", user.getId())
                        .executeUpdate();
                session.getTransaction().commit();
            } catch (Exception e) {
                session.getTransaction().rollback();
            }
        }
    }

    public void delete(int userId) {
        try (Session session = sf.openSession()) {
            try {
                session.beginTransaction();
                session.createQuery("delete User where id = :fId")
                        .setParameter("fId", userId)
                        .executeUpdate();
            } catch (Exception e) {
                session.getTransaction().rollback();
            }
        }
    }

    public List<User> findAllOrderById() {
        List<User> list = new ArrayList<>();
        try (Session session = sf.openSession()) {
            try {
                session.beginTransaction();
                list = session.createQuery("from User order by id", User.class).list();
            } catch (Exception e) {
                session.getTransaction().rollback();
            }
        }
        return list;
    }

    public Optional<User> findById(int userId) {
        Optional<User> optionalUser= Optional.empty();
        try (Session session = sf.openSession()) {
            try {
                session.beginTransaction();
                optionalUser = Optional.ofNullable(session.createQuery("from User where id = :fUserId", User.class)
                        .setParameter("fUserId", userId).getSingleResult());
            } catch (Exception e) {
                session.getTransaction().rollback();
            }
        }
        return optionalUser;
    }

    public List<User> findByLikeLogin(String key) {
        List<User> list = new ArrayList<>();
        try (Session session = sf.openSession()) {
            try {
                session.beginTransaction();
                list = session.createQuery("from User where login like :fKey", User.class)
                        .setParameter("fKey", "%" + key + "%")
                        .list();
            } catch (Exception e) {
                session.getTransaction().rollback();
            }
        }
        return list;
    }

    public Optional<User> findByLogin(String login) {
        Optional<User> optionalUser = Optional.empty();
        try (Session session = sf.openSession()) {
            try {
                session.beginTransaction();
                optionalUser = Optional.ofNullable(session.createQuery("from User where login like :fKey", User.class)
                        .setParameter("fKey", login).getSingleResult());
            } catch (Exception e) {
                session.getTransaction().rollback();
            }
        }
        return optionalUser;
    }
}
