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

    private final UserRepository userRepository;

    public Optional<User> getUserByCookie(Cookie cookie) {
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
