package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.job4j.cars.exception.CreateUuidException;
import ru.job4j.cars.exception.RegistrationUserException;
import ru.job4j.cars.model.Uuid;
import ru.job4j.cars.repository.UuidRepository;

@Service
@AllArgsConstructor
public class UuidService {

    private final UuidRepository repository;

    private final UserService userService;

    private final static Logger LOGGER = LoggerFactory.getLogger(UuidService.class);

    public Uuid create(Uuid uuid) throws RegistrationUserException {
        repository.create(uuid);
        if (uuid.getId() == null) {
            LOGGER.error("Error in create uuid method. Uuid entity was not created",
                    new CreateUuidException("Uuid entity was not created"));
            throw new RegistrationUserException("User was not created. Try again");
        }
        return uuid;
    }

}
