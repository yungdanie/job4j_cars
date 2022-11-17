package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Uuid;

import java.util.function.Consumer;

@Repository
@AllArgsConstructor
public class UuidRepository {

    private final MainRepository mainRepository;

    public Uuid create(Uuid uuid) {
        mainRepository.tx((Consumer<Session>) session -> session.persist(uuid));
        return uuid;
    }

    public void annulUuidKey(Uuid uuid) {
        mainRepository.tx((Consumer<Session>) session -> session.delete(uuid));
    }
}
