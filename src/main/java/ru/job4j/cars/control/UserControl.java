package ru.job4j.cars.control;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.job4j.cars.model.User;
import ru.job4j.cars.model.UuidEntity;
import ru.job4j.cars.service.UserService;

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

    @GetMapping("/loginUser")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest req, HttpServletResponse res, HttpSession session) {
        Optional<User> regUser = userService.authentication(user);
        if (regUser.isEmpty()) {
            model.addAttribute("fail_login", true);
            return "loginPage";
        }
        User detachedUser = regUser.get();
        String userAgent = req.getHeader("user-agent");
        if (detachedUser.getUuids()
                .stream()
                        .noneMatch(uuidEntity
                                -> uuidEntity.getUserAgent().equals(userAgent))) {
            UuidEntity uuidEntity = new UuidEntity();
            uuidEntity.setUuid(UUID.randomUUID());
            uuidEntity.setUserAgent(userAgent);
            detachedUser.getUuids().add(new UuidEntity());
            userService.updateUser(detachedUser);
        }
        session.setAttribute("regUser", detachedUser);
        return "index";
    }

    @GetMapping("/registrationUser")
    public String registrationUser(@ModelAttribute User user, Model model, HttpServletRequest req, HttpSession session) {
        Optional<User> regUser = userService.authentication(user);
        if (regUser.isPresent()) {
            model.addAttribute("fail_reg", true);
            return "regPage";
        }
        UuidEntity uuidEntity = new UuidEntity();
        uuidEntity.setUserAgent(req.getHeader("user-agent"));
        user.setUuids(Set.of(uuidEntity));
        userService.reg(user);
        return "index";
    }
}
