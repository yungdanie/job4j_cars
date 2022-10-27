package ru.job4j.cars.repository;


import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Component;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class MainRepository {
    private final SessionFactory sf;

    public <T> T tx(Function<Session, T> command) {
        Session session = sf.openSession();
        try (session) {
            var tx = session.beginTransaction();
            T rsl = command.apply(session);
            tx.commit();
            return rsl;
        } catch (Exception e) {
            var tx = session.getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    public void tx(Consumer<Session> command) {
        tx(session -> {
            command.accept(session);
            return null;
        });
    }

    public <T> Optional<T> getUniqResult(String query, Map<String, Object> args, Class<T> tClass) {
        Function<Session, Optional<T>> command = session -> {
            var sq = session.createQuery(query, tClass);
            for (Map.Entry<String, Object> arg : args.entrySet()) {
                sq.setParameter(arg.getKey(), arg.getValue());
            }
            return sq.uniqueResultOptional();
        };
        return tx(command);
    }

    public <T> int getResult(String query, Map<String, Object> args, Class<T> tClass) {
        Function<Session, Integer> command = session -> {
            var sq = session.createQuery(query, tClass);
            for (Map.Entry<String, Object> arg : args.entrySet()) {
                sq.setParameter(arg.getKey(), arg.getValue());
            }
            return sq.executeUpdate();
        };
        return tx(command);
    }

    public <T> List<T> getList(String query, Map<String, Object> args, Class<T> tClass) {
        Function<Session, List<T>> command = session -> {
            var sq = session.createQuery(query, tClass);
            for (Map.Entry<String, Object> arg : args.entrySet()) {
                sq.setParameter(arg.getKey(), arg.getValue());
            }
            return sq.list();
        };
        return tx(command);
    }

    public <T> List<T> getList(String query, Class<T> tClass) {
        return getList(query, Map.of(), tClass);
    }
}
