package ru.job4j.cars.advice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.job4j.cars.exception.LogoutUserException;
import ru.job4j.cars.exception.RegistrationUserException;
import ru.job4j.cars.exception.UndefinedUserException;

@ControllerAdvice
public class UserAdvice {

    private final String errorPageLink;

    private final String errorModelName;

    public UserAdvice(@Value("${ERROR_PAGE_LINK}") String errorPageLink,
                      @Value("${ERROR_MODEL_NAME}") String errorModelName) {
        this.errorModelName = errorModelName;
        this.errorPageLink = errorPageLink;
    }

    @ExceptionHandler(UndefinedUserException.class)
    public String handleUndefinedUserException(UndefinedUserException e, Model model) {
        model.addAttribute(errorModelName, e.getMessage());
        return errorPageLink;
    }

    @ExceptionHandler(LogoutUserException.class)
    public String handleLogoutUserException(LogoutUserException e, Model model) {
        model.addAttribute(errorModelName, e.getMessage());
        return errorPageLink;
    }

    @ExceptionHandler(RegistrationUserException.class)
    public String handleLogoutUserException(RegistrationUserException e, Model model) {
        model.addAttribute(errorModelName, e.getMessage());
        return errorPageLink;
    }
}
