package ru.job4j.cars.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.UserRepository;

import javax.servlet.http.Cookie;
import java.util.Optional;
import java.util.Properties;

@Service
public class UserService {

    private final String cookieUuidName;
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository, @Qualifier("serviceTerms") Properties properties) {
        this.userRepository = userRepository;
        cookieUuidName = properties.getProperty("COOKIE_UUID_NAME");
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
