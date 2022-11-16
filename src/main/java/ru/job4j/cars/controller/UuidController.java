package ru.job4j.cars.controller;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.job4j.cars.model.User;
import ru.job4j.cars.model.Uuid;
import ru.job4j.cars.service.UuidService;

import javax.servlet.http.HttpServletRequest;
import java.util.Properties;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class UuidController {

    private final UuidService uuidService;

    private final String userAgentHeader;

    private final String uuidUserCookieName;

    public UuidController(UuidService uuidService, @Qualifier("serviceTerms") Properties properties) {
        this.uuidService = uuidService;
        userAgentHeader = properties.getProperty("USER_AGENT_HEADER");
        uuidUserCookieName = properties.getProperty("UUID_USER_COOKIE_NAME");
    }

    @PostMapping("/createUuid")
    public String createUuid(@ModelAttribute User newUser, HttpServletRequest req) {
        UUID uuid = UUID.randomUUID();
        Uuid uuidEntity = new Uuid();
        uuidEntity.setUuid(uuid);
        uuidEntity.setUser(newUser);
        uuidEntity.setUserAgent(req.getHeader(userAgentHeader));
        uuidService.create(uuidEntity);
        return "index";
    }
}
