package ru.job4j.cars.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cars.exception.RegistrationUserException;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.UserRepository;

import javax.servlet.http.Cookie;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final String cookieUuidName;

    public UserService(UserRepository userRepository, @Value("COOKIE_UUID_NAME") String cookieUuidName) {
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
        if (!cookie.getName().equals(cookieUuidName)) {
            throw new IllegalArgumentException("Cookie that used to get User has wrong name");
        }
        return userRepository.getUserByCookie(cookie);
    }

    public User reg(User user) throws RegistrationUserException {
        userRepository.create(user);
        if (user.getId() == null) {
            throw new RegistrationUserException("User entity was not created");
        }
        return user;
    }

    public Optional<User> authentication(User user) {
        return userRepository.authentication(user);
    }

    public void updateUser(User user) {
        userRepository.mergeUser(user);
    }

}
