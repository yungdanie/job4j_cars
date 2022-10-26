package ru.job4j.cars.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void addCookie(String cookieName, String value, HttpServletResponse res) {
        res.addCookie(new Cookie(cookieName, value));
    }
}
