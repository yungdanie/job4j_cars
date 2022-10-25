package ru.job4j.cars.util;

import ru.job4j.cars.model.User;

import javax.servlet.http.HttpSession;

public class authUserUtil {

    public static User getUser(HttpSession httpSession) {
        return (User) httpSession.getAttribute("user");
    }
}
