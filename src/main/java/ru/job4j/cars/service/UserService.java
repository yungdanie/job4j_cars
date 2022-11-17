package ru.job4j.cars.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cars.exception.LogoutUserException;
import ru.job4j.cars.exception.RegistrationUserException;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.UserRepository;

import javax.servlet.http.Cookie;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final String cookieUuidName;

    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, @Value("UUID_USER_COOKIE_NAME") String cookieUuidName) {
        this.userRepository = userRepository;
        this.cookieUuidName = cookieUuidName;
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    public void annulUuidKey(Integer userId, Cookie cookie) {
        userRepository.annulUuidKey(userId, cookie);
    }

    public boolean checkAuth(User user) {
        return userRepository.checkAuth(user);
    }

    public Optional<User> getUserByCookie(Cookie cookie) {
        return userRepository.getUserByCookie(cookie);
    }

    public User reg(User user) throws RegistrationUserException {
        userRepository.create(user);
        if (user.getId() == null) {
            LOGGER.error("Error in registration User method. User was not created");
            throw new RegistrationUserException("User was not created");
        }
        return user;
    }

    public User loadUuids(User user) throws LogoutUserException {
        Optional<User> loadUser = userRepository.loadUuids(user);
        if (loadUser.isEmpty()) {
            LOGGER.trace("Error in loadUuids method. User was not found");
            throw new LogoutUserException("User was not found");
        }
        return loadUser.get();
    }

    public Optional<User> authentication(User user) {
        return userRepository.authentication(user);
    }

    public void updateUser(User user) {
        userRepository.mergeUser(user);
    }

}
