package ru.job4j.cars.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static void setCookie(HttpServletResponse response, String key, String value, int expiry) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(expiry);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public static void deleteCookie(Cookie cookie) {
        cookie.setValue("");
        cookie.setMaxAge(0);
    }
}
