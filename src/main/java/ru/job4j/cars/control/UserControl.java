package ru.job4j.cars.control;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.cars.exceptions.UndefinedCookieException;
import ru.job4j.cars.model.User;
import ru.job4j.cars.model.UuidEntity;
import ru.job4j.cars.service.UserService;
import ru.job4j.cars.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserControl {

    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserControl.class);

    private final static String UUID_USER_COOKIE_NAME = "user_uuid";

    private final static String FAIL_LOGIN_MODEL_NAME = "fail_login";

    private final static String FAIL_REG_MODEL_NAME = "fail_reg";
    private final static String USER_SESSION_NAME = "actual_user";

    private final static String USER_MODEL_NAME = "regUser";

    private final static String USER_AGENT_HEADER = "user-agent";
    private final static int COOKIE_EXPIRE_TIME = 60 * 60 * 24 * 7;

    @GetMapping("/loginUser")
    public String loginUser() {
        return "loginPage";
    }

    @PostMapping("/loginUser")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest req, HttpServletResponse res, HttpSession session) {
        Optional<User> regUser = userService.authentication(user);
        if (regUser.isEmpty()) {
            model.addAttribute(FAIL_LOGIN_MODEL_NAME, true);
            return "loginPage";
        }
        User detachedUser = regUser.get();
        String userAgent = req.getHeader(USER_AGENT_HEADER);
        Optional<UuidEntity> uuid = detachedUser.
                getUuids().
                stream().
                filter(uuidEntity -> uuidEntity.getUserAgent().equals(userAgent)).findFirst();
        if (uuid.isEmpty()) {
            UuidEntity newUuid = new UuidEntity();
            newUuid.setUuid(UUID.randomUUID());
            newUuid.setUserAgent(userAgent);
            detachedUser.getUuids().add(newUuid);
            userService.updateUser(detachedUser);
            CookieUtil.setCookie(res, UUID_USER_COOKIE_NAME, newUuid.getUuid().toString(), COOKIE_EXPIRE_TIME);
        } else {
            CookieUtil.setCookie(res, UUID_USER_COOKIE_NAME, uuid.get().getUuid().toString(), COOKIE_EXPIRE_TIME);
        }
        session.setAttribute(USER_SESSION_NAME, detachedUser);
        model.addAttribute(USER_MODEL_NAME, detachedUser);
        return "index";
    }

    @GetMapping("/registrationUser")
    public String registrationUser() {
        return "regPage";
    }

    @PostMapping("/registrationUser")
    public String registrationUser(@ModelAttribute User newUser, Model model, HttpServletRequest req, HttpServletResponse res, HttpSession session) {
        if (userService.checkAuth(newUser)) {
            model.addAttribute(FAIL_REG_MODEL_NAME, true);
            return "regPage";
        }
        UuidEntity newUuid = new UuidEntity();
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

    @GetMapping("/logoutUser/{id}")
    public String logoutUser(HttpServletRequest req, @PathVariable Integer id) {
        Optional<Cookie> cookieOptional = Arrays.stream(req.getCookies()).
                filter(cookie -> cookie.getName().equals(UUID_USER_COOKIE_NAME)).findFirst();
        try {
            if (cookieOptional.isEmpty()) {
                throw new UndefinedCookieException("User uuid was not found in cookie");
            }
        } catch (UndefinedCookieException e) {
            LOGGER.error("Error in logoutUser method", e);
        }
        userService.annulUuidKey(id, cookieOptional.get());
        return "index";
    }
}
