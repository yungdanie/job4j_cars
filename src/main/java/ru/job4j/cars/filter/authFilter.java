package ru.job4j.cars.filter;

import org.springframework.stereotype.Component;
import ru.job4j.cars.model.User;
import ru.job4j.cars.util.authUserUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class authFilter implements Filter {

    private final static String NO_USER_REDIRECT = "login";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        User user = authUserUtil.getUser(session);
        if (user == null) {
            res.sendRedirect(req.getContextPath() + NO_USER_REDIRECT);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

}
