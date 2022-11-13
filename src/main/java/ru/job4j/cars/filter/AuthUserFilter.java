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

    private final Properties properties;

    private final String session_user_name = properties.getProperty("SESSION_USER_NAME");

    private  final String no_user_redirect_link = properties.getProperty("NO_USER_REDIRECT_LINK");

    private final String auth_user_redirect_link = properties.getProperty("AUTH_USER_REDIRECT_LINK");

    private final List<String> no_user_access_restriction = accessRestrictionsParser(properties.getProperty("NO_USER_ACCESS_RESTRICTION"));

    private final List<String> auth_user_access_restriction = accessRestrictionsParser(properties.getProperty(auth_user_access_restriction));
            "/loginUser", "/registrationUser"
    );
    
    private List<String> accessRestrictionsParser(String stringList) {
        return List.of(stringList.split(","));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute(session_user_name);
        if (AuthUserUtil.isUserGuestOrNull(user)
                && no_user_access_restriction.contains(req.getRequestURI())) {
            res.sendRedirect(req.getContextPath() + no_user_redirect_link);
        } else if (AuthUserUtil.isUserLogged(user)
                && auth_user_access_restriction.contains(req.getRequestURI())) {
            res.sendRedirect(req.getContextPath() + auth_user_redirect_link);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
