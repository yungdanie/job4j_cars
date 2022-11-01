package ru.job4j.cars.filter;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.UserService;
import ru.job4j.cars.util.AuthUserUtil;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static ru.job4j.cars.util.AuthUserUtil.setUserGuest;

@AllArgsConstructor
public class UserSessionFilter implements Filter {

    private final UserService userService;

    private static final String SESSION_USER_NAME = "actual_user";

    private static final String COOKIE_UUID_USER_NAME = "user_uuid";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        Cookie[] cookies = req.getCookies();
        User user = AuthUserUtil.getUser(session);
        Optional<Cookie> cookieOptional = Arrays
                .stream(cookies)
                .filter(cookie -> cookie.getName().equals(COOKIE_UUID_USER_NAME))
                .findFirst();
        if (user == null) {
            if (cookieOptional.isPresent()) {
                Optional<User> loadUser = userService.getUserByCookie(cookieOptional.get());
                if (loadUser.isPresent()) {
                    session.setAttribute(SESSION_USER_NAME, loadUser.get());
                } else {
                    setUserGuest(session);
                }
            } else {
                setUserGuest(session);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
