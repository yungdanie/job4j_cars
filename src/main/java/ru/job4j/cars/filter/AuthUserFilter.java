package ru.job4j.cars.filter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.UserService;
import ru.job4j.cars.util.AuthUserUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Component
@AllArgsConstructor
public class AuthUserFilter implements Filter {

    private final UserService userService;

    private static final String GUEST_REDIRECT = "/index";

    private static final String REDIRECT_LINK = "/login";

    private static final List<String> ACCESS_RESTRICTION = List.of(
            "addPost"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("actual_uses");
        if (AuthUserUtil.isUserGuest(user)
                && ACCESS_RESTRICTION.contains(req.getRequestURI())) {
            res.sendRedirect(req.getContextPath() + REDIRECT_LINK);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
