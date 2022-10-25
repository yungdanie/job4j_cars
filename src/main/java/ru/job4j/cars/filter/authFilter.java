package ru.job4j.cars.filter;

import org.springframework.stereotype.Component;
import ru.job4j.cars.model.User;
import ru.job4j.cars.util.authUserUtil;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Component
public class authFilter implements Filter {

    private final static String NO_USER_REDIRECT = "login";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        User user = authUserUtil.getUser(session);
        Cookie[] cookies = req.getCookies();
        Optional<Cookie> cookieOptional = Arrays
                .stream(cookies)
                .filter(cookie -> cookie.getName().equals("user_uuid"))
                .findFirst();
        if (user == null) {
            User newUser = new User();
            newUser.setUuid(new UUID[] {UUID.randomUUID()});
            req.getSession().setAttribute("actual_user", newUser);
        } else {
            if (cookieOptional.isPresent() && cookieOptional.get().getValue().equals(user.getUuid().toString())) {

            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
