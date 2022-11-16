package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.exception.CreateUuidException;
import ru.job4j.cars.model.Uuid;
import ru.job4j.cars.repository.UuidRepository;

@Service
@AllArgsConstructor
public class UuidService {

    private final UuidRepository repository;

    private final UserService userService;

    public Uuid create(Uuid uuid) {
        try {
            return repository.create(uuid);
        } catch (CreateUuidException e) {
            userService.deleteUser(uuid.getUser());
            return uuid;
        }
    }

}
