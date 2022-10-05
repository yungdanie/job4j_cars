package ru.job4j.cars;

import org.assertj.core.api.Assertions;
import org.hibernate.Session;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.MainRepository;
import ru.job4j.cars.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class UserHibernateTest {

    private final MainRepository mainRepository = new MainRepository(new MetadataSources(new StandardServiceRegistryBuilder()
            .configure().build())
            .buildMetadata().buildSessionFactory());
    private final UserRepository userRepository = new UserRepository(mainRepository);

    @BeforeEach
    public void clearDataBase() {
        mainRepository.tx((Function<Session, Object>) session -> session.createQuery("delete from User").executeUpdate());
    }

    @Test
    public void whenSaveUser() {
        User user = new User();
        user.setLogin("login");
        userRepository.create(user);
        Assertions.assertThat(userRepository.findById(user.getId()).orElse(new User())).isEqualTo(user);
    }

    @Test
    public void whenUpdateUser() {
        User user = new User();
        user.setLogin("login");
        userRepository.create(user);
        String newLogin = "new_login";
        user.setLogin(newLogin);
        userRepository.update(user);
        Assertions.assertThat(userRepository.findById(user.getId()).orElse(new User())).isEqualTo(user);
    }

    @Test
    public void whenDeleteUser() {
        User user = new User();
        user.setLogin("login");
        userRepository.create(user);
        userRepository.delete(user);
        Assertions.assertThat(userRepository.findById(user.getId())).isEqualTo(Optional.empty());
    }

    @Test
    public void whenFindByLogin() {
        User user = new User();
        String login = "login";
        user.setLogin(login);
        userRepository.create(user);
        Assertions.assertThat(userRepository.findByLogin(login).orElse(new User())).isEqualTo(user);
    }

    @Test
    public void whenFindLikeLogin() {
        User firstUser = new User();
        User secondUser = new User();
        String key = "like";
        firstUser.setLogin("like_login");
        secondUser.setLogin("login_like");
        userRepository.create(firstUser);
        userRepository.create(secondUser);
        Assertions.assertThat(userRepository.findByLikeLogin(key)).isEqualTo(List.of(firstUser, secondUser));
    }
}
