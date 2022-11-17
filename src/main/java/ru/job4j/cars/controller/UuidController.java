package ru.job4j.cars.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.job4j.cars.exception.RegistrationUserException;
import ru.job4j.cars.model.User;
import ru.job4j.cars.model.Uuid;
import ru.job4j.cars.service.UuidService;
import ru.job4j.cars.util.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

@Controller
public class UuidController {

    private final UuidService uuidService;

    private final String userAgentHeader;

    private final String uuidUserCookieName;

    private final String userModelName;

    private final String sessionUserName;
    private final int cookieExpireTime;

    private final static Logger LOGGER = LoggerFactory.getLogger(UuidController.class);

    public UuidController(UuidService uuidService,
                          @Value("${USER_AGENT_HEADER}") String userAgentHeader,
                          @Value("${UUID_USER_COOKIE_NAME}") String uuidUserCookieName,
                          @Value("${USER_MODEL_NAME}") String userModelName,
                          @Value("${COOKIE_EXPIRE_TIME}") String cookieExpireTime,
                          @Value("${SESSION_USER_NAME}") String sessionUserName) {
        this.uuidService = uuidService;
        this.userAgentHeader = userAgentHeader;
        this.uuidUserCookieName = uuidUserCookieName;
        this.userModelName = userModelName;
        try {
            this.cookieExpireTime = Integer.parseInt(cookieExpireTime);
        } catch (NumberFormatException e) {
            LOGGER.error("Error initialization Uuid Controller. Cookie expire time has wrong format");
            throw new RuntimeException("Cookie expire time has wrong format", e);
        }
        this.sessionUserName = sessionUserName;
    }

    @PostMapping("/regUuid")
    public String createUuid(@ModelAttribute("user") User user,
                             HttpServletRequest req,
                             HttpServletResponse res,
                             HttpSession session,
                             Model model) throws RegistrationUserException {
        UUID uuid = UUID.randomUUID();
        Uuid uuidEntity = new Uuid();
        uuidEntity.setUuid(uuid);
        uuidEntity.setUser(user);
        uuidEntity.setUserAgent(req.getHeader(userAgentHeader));
        uuidService.regUuid(uuidEntity);
        CookieUtil.setCookie(res, uuidUserCookieName, uuid.toString(), cookieExpireTime);
        session.setAttribute(sessionUserName, user);
        model.addAttribute(userModelName, user);
        return "redirect:/index";
    }

    @PostMapping("/annulUuid")
    public String annulUuid(@ModelAttribute("cookie") Cookie cookieUuid, @ModelAttribute("uuid") Uuid uuid) {
        uuidService.annulUuidKey(uuid);
        CookieUtil.deleteCookie(cookieUuid);
        return "redirect:/index";
    }
}
