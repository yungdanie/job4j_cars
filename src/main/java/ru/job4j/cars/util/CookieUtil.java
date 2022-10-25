package ru.job4j.cars.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class CookieUtil {

    public static void addCookie(String cookieName, String value, HttpServletResponse res) {
        res.addCookie(new Cookie(cookieName, value));
    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID());
    }
}
