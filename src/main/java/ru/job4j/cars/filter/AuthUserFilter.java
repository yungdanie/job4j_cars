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
import java.util.Properties;

@AllArgsConstructor
public class AuthUserFilter implements Filter {
    private final String sessionUserName;

    private  final String noUserRedirectLink;

    private final String authUserRedirectLink;

    private final List<String> noUserAccessRestriction;

    private final List<String> authUserAccessRestriction;

    public AuthUserFilter(Properties properties) {
        noUserAccessRestriction = accessRestrictionsParser(properties.getProperty("NO_USER_ACCESS_RESTRICTION"));
        authUserAccessRestriction = accessRestrictionsParser(properties.getProperty("AUTH_USER_ACCESS_RESTRICTION"));
        authUserRedirectLink = properties.getProperty("AUTH_USER_REDIRECT_LINK");
        noUserRedirectLink = properties.getProperty("NO_USER_REDIRECT_LINK");
        sessionUserName = properties.getProperty("SESSION_USER_NAME");
    }

    private List<String> accessRestrictionsParser(String stringList) {
        return List.of(stringList.split(","));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(sessionUserName);
        if (AuthUserUtil.isUserGuestOrNull(user)
                && noUserAccessRestriction.contains(req.getRequestURI())) {
            res.sendRedirect(req.getContextPath() + noUserRedirectLink);
        } else if (AuthUserUtil.isUserLogged(user)
                && authUserAccessRestriction.contains(req.getRequestURI())) {
            res.sendRedirect(req.getContextPath() + authUserRedirectLink);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
