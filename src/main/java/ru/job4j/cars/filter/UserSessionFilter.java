package ru.job4j.cars.filter;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.UserService;
import ru.job4j.cars.util.AuthUserUtil;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static ru.job4j.cars.util.AuthUserUtil.setUserGuest;

@AllArgsConstructor
public class UserSessionFilter implements Filter {

    private final UserService userService;

    private final String sessionUserName;

    private final String cookieUuidUserName;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpSession session = req.getSession();
        Cookie[] cookies = req.getCookies();
        User user = AuthUserUtil.getUser(session);
        if (user != null) {
            session.setAttribute(sessionUserName, user);
        } else if (cookies != null && cookies.length != 0) {
            Optional<Cookie> cookieOptional = Arrays
                    .stream(cookies)
                    .filter(cookie -> cookie.getName().equals(cookieUuidUserName))
                    .findFirst();
            if (cookieOptional.isPresent()) {
                Optional<User> loadUser = userService.getUserByCookie(cookieOptional.get());
                if (loadUser.isPresent()) {
                    session.setAttribute(sessionUserName, loadUser.get());
                } else {
                    setUserGuest(session);
                }
            } else {
                setUserGuest(session);
            }
        } else {
            setUserGuest(session);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
