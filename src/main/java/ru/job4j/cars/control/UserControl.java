package ru.job4j.cars.control;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.job4j.cars.model.User;
import ru.job4j.cars.service.UserService;
import ru.job4j.cars.util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class UserControl {

    private final UserService userService;

    @GetMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest req, HttpServletResponse res) {
        Optional<User> regUser = userService.authentication(user);
        if (regUser.isEmpty()) {
            model.addAttribute("fail_login", true);
            return "loginPage";
        }
        User detachedUser = regUser.get();
        detachedUser.setUuid(new UUID[] {UUID.randomUUID()});
        req.getSession().setAttribute("regUser", detachedUser);
        return "index";
    }
}
