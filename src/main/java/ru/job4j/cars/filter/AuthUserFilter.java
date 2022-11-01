package ru.job4j.cars.filter;

import lombok.AllArgsConstructor;
import ru.job4j.cars.model.User;
import ru.job4j.cars.util.AuthUserUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
public class AuthUserFilter implements Filter {

    private static final String SESSION_USER_NAME = "actual_user";

    private static final String NO_USER_REDIRECT_LINK = "/loginUser";

    private static final String AUTH_USER_REDIRECT_LINK = "/index";

    private static final List<String> NO_USER_ACCESS_RESTRICTION = List.of(
            "/addPost"
    );

    private static final List<String> AUTH_USER_ACCESS_RESTRICTION = List.of(
            "/loginUser", "registrationUser"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(SESSION_USER_NAME);
        if (AuthUserUtil.isUserGuestOrNull(user)
                && NO_USER_ACCESS_RESTRICTION.contains(req.getRequestURI())) {
            res.sendRedirect(req.getContextPath() + NO_USER_REDIRECT_LINK);
        } else if (AuthUserUtil.isUserLogged(user)
                && AUTH_USER_ACCESS_RESTRICTION.contains(req.getRequestURI())) {
            res.sendRedirect(req.getContextPath() + AUTH_USER_REDIRECT_LINK);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
