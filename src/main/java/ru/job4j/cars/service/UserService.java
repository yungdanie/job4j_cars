package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.UserRepository;

import javax.servlet.http.Cookie;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private static final String COOKIE_UUID_NAME = "user_uuid";
    private final UserRepository userRepository;

    public void annulUuidKey(Integer userId, Cookie cookie) {
        if (!cookie.getName().equals(COOKIE_UUID_NAME)) {
            throw new IllegalArgumentException("Cookie that used to get User has wrong name");
        }
        userRepository.annulUuidKey(userId, cookie);
    }

    public boolean checkAuth(User user) {
        return userRepository.checkAuth(user);
    }

    public Optional<User> getUserByCookie(Cookie cookie) {
        if (!cookie.getName().equals(COOKIE_UUID_NAME)) {
            throw new IllegalArgumentException("Cookie that used to get User has wrong name");
        }
        return userRepository.getUserByCookie(cookie);
    }

    public User reg(User user) {
        return userRepository.create(user);
    }

    public Optional<User> authentication(User user) {
        return userRepository.authentication(user);
    }

    public void updateUser(User user) {
        userRepository.mergeUser(user);
    }

}
