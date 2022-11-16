package ru.job4j.cars.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cars.exception.LogoutUserException;
import ru.job4j.cars.exception.RegistrationUserException;
import ru.job4j.cars.exception.UndefinedCookieException;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.UserService;
import ru.job4j.cars.util.AuthUserUtil;
import ru.job4j.cars.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Optional;

@Controller
public class UserController {

    private final UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final String uuidUserCookieName;

    private final String failLoginModelName;

    private final String failRegModelName;

    public UserController(UserService userService,
                          @Value("${UUID_USER_COOKIE_NAME}") String uuidUserCookieName,
                          @Value("${FAIL_LOGIN_MODEL_NAME}") String failLoginModelName,
                          @Value("${FAIL_REG_MODEL_NAME}") String failRegModelName) {
        this.userService = userService;
        this.uuidUserCookieName = uuidUserCookieName;
        this.failLoginModelName = failLoginModelName;
        this.failRegModelName = failRegModelName;
    }

    @GetMapping("/loginUser")
    public String loginUser() {
        return "user/loginPage";
    }

    @PostMapping("/loginUser")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest req,
                            HttpServletResponse res, HttpSession session,
                            RedirectAttributes redirectAttributes) {
        Optional<User> regUser = userService.authentication(user);
        if (regUser.isEmpty()) {
            model.addAttribute(failLoginModelName, true);
            return "user/loginPage";
        }
        User detachedUser = regUser.get();
        redirectAttributes.addFlashAttribute("user", detachedUser);
        req.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return "redirect:/regUuid";
    }

    @GetMapping("/registrationUser")
    public String registrationUser() {
        return "user/regPage";
    }

    @PostMapping("/registrationUser")
    public String registrationUser(@ModelAttribute User newUser, Model model, HttpServletRequest req,
                                   RedirectAttributes redirectAttributes) throws RegistrationUserException {
        if (userService.checkAuth(newUser)) {
            model.addAttribute(failRegModelName, true);
            return "user/regPage";
        }
        userService.reg(newUser);
        redirectAttributes.addFlashAttribute("user", newUser);
        req.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return "redirect:/regUuid";
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
