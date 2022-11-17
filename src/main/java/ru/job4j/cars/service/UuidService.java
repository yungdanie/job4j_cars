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

    private final UuidRepository uuidRepository;

    private final UserService userService;

    private final static Logger LOGGER = LoggerFactory.getLogger(UuidService.class);

    public Uuid regUuid(Uuid uuid) throws RegistrationUserException {
        uuidRepository.create(uuid);
        if (uuid.getId() == null) {
            LOGGER.error("Error in create uuid method. Uuid entity was not created",
                    new CreateUuidException("Uuid entity was not created"));
            throw new RegistrationUserException("User was not created. Try again");
        }
        return uuid;
    }

    public void annulUuidKey(Uuid uuid) {
        if (uuid.getId() == null) {
            LOGGER.info("Uuid was deleted before this");
            return;
        }
        uuidRepository.annulUuidKey(uuid);
    }



}
