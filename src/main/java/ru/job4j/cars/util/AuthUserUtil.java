package ru.job4j.cars.util;

import org.springframework.ui.Model;
import ru.job4j.cars.model.User;
import ru.job4j.cars.model.UuidEntity;

import javax.servlet.http.HttpSession;

public class AuthUserUtil {

    private static final String SESSION_USER_NAME = "actual_user";

    private static final String MODEL_USER_NAME = "regUser";

    private static final String GUEST_USER_NAME = "Гость";

    public static void addUserToModel(HttpSession httpSession, Model model) {
        model.addAttribute(MODEL_USER_NAME, httpSession.getAttribute(SESSION_USER_NAME));
    }

    public static User getUser(HttpSession httpSession) {
        return (User) httpSession.getAttribute(SESSION_USER_NAME);
    }

    public static void setUserGuest(HttpSession session) {
        User user = new User();
        UuidEntity uuidEntity = new UuidEntity();
        user.setLogin(GUEST_USER_NAME);
        session.setAttribute(SESSION_USER_NAME, user);
    }

    public static boolean isUserGuest(User user) {
        return user != null && user.getLogin().equals(GUEST_USER_NAME);
    }

    public static boolean isUserGuestOrNull(User user) {
        return user == null || user.getLogin().equals(GUEST_USER_NAME);
    }

}
