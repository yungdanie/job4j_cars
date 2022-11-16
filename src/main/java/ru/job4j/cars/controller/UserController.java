package ru.job4j.cars.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cars.exception.LogoutUserException;
import ru.job4j.cars.exception.UndefinedCookieException;
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
public class UserController {

    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final String uuidUserCookieName;

    private final String failLoginModelName;

    private final String failRegModelName;
    private final String sessionUserName;

    private final String userModelName;

    private final String userAgentHeader;
    private final int cookieExpireTime;

    private final String errorPageLink;

    public UserController(UserService userService, @Qualifier("serviceTerms") Properties properties) {
        this.userService = userService;
        uuidUserCookieName = properties.getProperty("UUID_USER_COOKIE_NAME");
        failLoginModelName = properties.getProperty("FAIL_LOGIN_MODEL_NAME");
        failRegModelName = properties.getProperty("FAIL_REG_MODEL_NAME");
        sessionUserName = properties.getProperty("SESSION_USER_NAME");
        userModelName = properties.getProperty("USER_MODEL_NAME");
        userAgentHeader = properties.getProperty("USER_AGENT_HEADER");
        errorPageLink = properties.getProperty("ERROR_PAGE_LINK");
        cookieExpireTime = expireTimeParser(properties.getProperty("COOKIE_EXPIRE_TIME"));
    }

    private int expireTimeParser(String expireTime) {
        List<String> nums = List.of(expireTime.split(" "));
        return nums.stream().map(Integer::valueOf).reduce(1, (x, y) -> x * y);
    }

    @GetMapping("/loginUser")
    public String loginUser() {
        return "user/loginPage";
    }

    @PostMapping("/loginUser")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest req, HttpServletResponse res, HttpSession session) {
        Optional<User> regUser = userService.authentication(user);
        if (regUser.isEmpty()) {
            model.addAttribute(failLoginModelName, true);
            return "user/loginPage";
        }
        User detachedUser = regUser.get();
        String userAgent = req.getHeader(userAgentHeader);
        Uuid newUuid = new Uuid();
        newUuid.setUuid(UUID.randomUUID());
        newUuid.setUserAgent(userAgent);
        detachedUser.getUuids().add(newUuid);
        userService.updateUser(detachedUser);
        CookieUtil.setCookie(res, uuidUserCookieName, newUuid.getUuid().toString(), cookieExpireTime);
        session.setAttribute(sessionUserName, detachedUser);
        model.addAttribute(userModelName, detachedUser);
        return "index";
    }

    @GetMapping("/registrationUser")
    public String registrationUser() {
        return "user/regPage";
    }

    @PostMapping("/registrationUser")
    public String registrationUser(@ModelAttribute User newUser, Model model, HttpServletRequest req,
                                   HttpServletResponse res, HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (userService.checkAuth(newUser)) {
            model.addAttribute(failRegModelName, true);
            return "user/regPage";
        }
        userService.reg(newUser);
        CookieUtil.setCookie(res, uuidUserCookieName, uuid.toString(), cookieExpireTime);
        session.setAttribute(sessionUserName, newUser);
        model.addAttribute(userModelName, newUser);
        redirectAttributes.addAttribute("newUser", newUser);
        req.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return "redirect:/createUuid";
    }

    @GetMapping("/logoutUser")
    public String logoutUser(HttpServletRequest req, HttpSession session, Model model) throws LogoutUserException, UndefinedCookieException {
        User sessionUser = AuthUserUtil.getUser(session);
        if (sessionUser == null) {
            LOGGER.error("Error in logoutUser method. Session is not associated with any user");
            throw new LogoutUserException("Session is not associated with any user");
        }
        Optional<Cookie> cookieOptional = Arrays.stream(req.getCookies()).
                filter(cookie -> cookie.getName().equals(uuidUserCookieName)).findFirst();
        if (cookieOptional.isEmpty()) {
            LOGGER.error("Error in logoutUser method. User cookie was not found while logout");
            throw new UndefinedCookieException("User cookie was not found while logout");
        } else {
            Cookie cookie = cookieOptional.get();
            if (sessionUser.getUuids()
                    .stream()
                    .anyMatch(uuid -> uuid.getUuid().toString()
                            .equals(cookie.getValue()))) {
                userService.annulUuidKey(sessionUser.getId(), cookie);
                CookieUtil.deleteCookie(cookie);
            } else {
                LOGGER.error("Error in logoutUser method. Uuid is not attached to the user");
                throw new UndefinedCookieException("Uuid is not attached to the user");
            }
        }
        AuthUserUtil.setUserGuest(session);
        return "redirect:/index";
    }
}
