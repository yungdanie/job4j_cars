package ru.job4j.cars.util;

import ru.job4j.cars.model.User;

import javax.servlet.http.HttpSession;

public class AuthUserUtil {

    public static User getUser(HttpSession httpSession) {
        return (User) httpSession.getAttribute("user");
    }

    public static void setUserGuest(HttpSession session) {
        User user = new User();
        user.setLogin("Гость");
        session.setAttribute("actual_user", user);
    }

}
