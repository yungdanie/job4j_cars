package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.exception.CreateUuidException;
import ru.job4j.cars.model.Uuid;

import java.util.function.Consumer;

@Repository
@AllArgsConstructor
public class UuidRepository {

    private final MainRepository mainRepository;

    public Uuid create(Uuid uuid) throws CreateUuidException {
        mainRepository.tx((Consumer<Session>) session -> session.persist(uuid));
        if (uuid.getId() == null) {
            throw new CreateUuidException("Uuid entity was not created");
        }
        return uuid;
    }
}
