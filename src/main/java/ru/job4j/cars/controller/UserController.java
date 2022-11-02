package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.cars.exception.LogoutUserException;
import ru.job4j.cars.model.User;
import ru.job4j.cars.model.Uuid;
import ru.job4j.cars.service.UserService;
import ru.job4j.cars.util.AuthUserUtil;
import ru.job4j.cars.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @Autowired
    @Qualifier("serviceTerms")
    private final Map<String, String> serviceTerms;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final static String UUID_USER_COOKIE_NAME = "user_uuid";

    private final static String FAIL_LOGIN_MODEL_NAME = "fail_login";

    private final static String FAIL_REG_MODEL_NAME = "fail_reg";
    private final static String USER_SESSION_NAME = "actual_user";

    private final static String USER_MODEL_NAME = "regUser";

    private final static String USER_AGENT_HEADER = "user-agent";
    private final static int COOKIE_EXPIRE_TIME = 60 * 60 * 24 * 7;

    private final static String ERROR_PAGE_LINK = "state/errorPage";

    @GetMapping("/loginUser")
    public String loginUser() {
        return "user/loginPage";
    }

    @PostMapping("/loginUser")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest req, HttpServletResponse res, HttpSession session) {
        Optional<User> regUser = userService.authentication(user);
        if (regUser.isEmpty()) {
            model.addAttribute(FAIL_LOGIN_MODEL_NAME, true);
            return "user/loginPage";
        }
        User detachedUser = regUser.get();
        String userAgent = req.getHeader(USER_AGENT_HEADER);
        Uuid newUuid = new Uuid();
        newUuid.setUuid(UUID.randomUUID());
        newUuid.setUserAgent(userAgent);
        detachedUser.getUuids().add(newUuid);
        userService.updateUser(detachedUser);
        CookieUtil.setCookie(res, UUID_USER_COOKIE_NAME, newUuid.getUuid().toString(), COOKIE_EXPIRE_TIME);
        session.setAttribute(USER_SESSION_NAME, detachedUser);
        model.addAttribute(USER_MODEL_NAME, detachedUser);
        return "index";
    }

    @GetMapping("/registrationUser")
    public String registrationUser() {
        return "user/regPage";
    }

    @PostMapping("/registrationUser")
    public String registrationUser(@ModelAttribute User newUser, Model model, HttpServletRequest req, HttpServletResponse res, HttpSession session) {
        if (userService.checkAuth(newUser)) {
            model.addAttribute(FAIL_REG_MODEL_NAME, true);
            return "user/regPage";
        }
        Uuid newUuid = new Uuid();
        UUID uuid = UUID.randomUUID();
        newUuid.setUuid(uuid);
        newUuid.setUserAgent(req.getHeader(USER_AGENT_HEADER));
        newUser.setUuids(Set.of(newUuid));
        userService.reg(newUser);
        CookieUtil.setCookie(res, UUID_USER_COOKIE_NAME, uuid.toString(), COOKIE_EXPIRE_TIME);
        session.setAttribute(USER_SESSION_NAME, newUser);
        model.addAttribute(USER_MODEL_NAME, newUser);
        return "index";
    }

    @GetMapping("/logoutUser")
    public String logoutUser(HttpServletRequest req, HttpSession session, Model model) throws LogoutUserException {
        User sessionUser = AuthUserUtil.getUser(session);
        if (sessionUser == null) {
            LOGGER.error("Error in logoutUser method. Session is not associated with any user");
            throw new LogoutUserException("Session is not associated with any user");
        }
        Optional<Cookie> cookieOptional = Arrays.stream(req.getCookies()).
                filter(cookie -> cookie.getName().equals(UUID_USER_COOKIE_NAME)).findFirst();
        if (cookieOptional.isEmpty()) {
            LOGGER.error("Error in logoutUser method. User cookie was not found while logout");
        } else {
            Cookie cookie = cookieOptional.get();
            userService.annulUuidKey(sessionUser.getId(), cookie);
            CookieUtil.deleteCookie(cookie);
        }
        AuthUserUtil.setUserGuest(session);
        return "redirect:/index";
    }
}
