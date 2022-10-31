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

    private static final String GUEST_REDIRECT = "/index";

    private static final String SESSION_USER_NAME = "actual_user";

    private static final String REDIRECT_LINK = "/loginUser";

    private static final List<String> ACCESS_RESTRICTION = List.of(
            "/addPost"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(SESSION_USER_NAME);
        if (AuthUserUtil.isUserGuestOrNull(user)
                && ACCESS_RESTRICTION.contains(req.getRequestURI())) {
            res.sendRedirect(req.getContextPath() + REDIRECT_LINK);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
