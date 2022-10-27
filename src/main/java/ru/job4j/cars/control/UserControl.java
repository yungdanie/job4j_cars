package ru.job4j.cars.control;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.job4j.cars.model.User;
import ru.job4j.cars.model.UuidEntity;
import ru.job4j.cars.service.UserService;
import ru.job4j.cars.util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserControl {

    private final UserService userService;

    private final static String USER_COOKIE_NAME = "actual_user";

    private final static String FAIL_LOGIN_MODEL_NAME = "fail_login";

    private final static String FAIL_REG_MODEL_NAME = "fail_reg";
    private final static String REG_USER_SESSION_NAME = "reg_user";
    private final static String USER_AGENT_HEADER = "user-agent";
    private final static int COOKIE_EXPIRE_TIME = 60 * 60 * 24 * 7;

    @GetMapping("/loginUser")
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
            CookieUtil.setCookie(res, USER_COOKIE_NAME, newUuid.getUuid().toString(), COOKIE_EXPIRE_TIME);
        } else {
            CookieUtil.setCookie(res, USER_COOKIE_NAME, uuid.get().getUuid().toString(), COOKIE_EXPIRE_TIME);
        }
        session.setAttribute(REG_USER_SESSION_NAME, detachedUser);
        return "index";
    }

    @GetMapping("/registrationUser")
    public String registrationUser(@ModelAttribute User newUser, Model model, HttpServletRequest req, HttpSession session) {
        if (userService.checkAuth(newUser)) {
            model.addAttribute(FAIL_REG_MODEL_NAME, true);
            return "regPage";
        } else {
            UuidEntity newUuid = new UuidEntity();
            newUuid.setUserAgent(req.getHeader(USER_AGENT_HEADER));
            newUser.setUuids(Set.of(newUuid));
            userService.reg(newUser);
        }
        session.setAttribute(REG_USER_SESSION_NAME, newUser);
        return "index";
    }
}
