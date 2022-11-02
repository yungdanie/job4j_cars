package ru.job4j.cars.advice;

import org.apache.catalina.connector.Response;
import org.hibernate.ObjectNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.job4j.cars.exception.LogoutUserException;
import ru.job4j.cars.exception.UndefinedCookieException;
import ru.job4j.cars.exception.UndefinedPostException;
import ru.job4j.cars.exception.UndefinedUserException;

@ControllerAdvice
public class DefaultAdvice {

    private final static String ERROR_PAGE_LINK = "state/errorPage";

    private final static String ERROR_MODEL_NAME = "errorMessage";

    @ExceptionHandler(UndefinedUserException.class)
    public String handleUndefinedUserException(UndefinedUserException e, Model model) {
        model.addAttribute(ERROR_MODEL_NAME, e.getMessage());
        return ERROR_PAGE_LINK;
    }

    @ExceptionHandler(UndefinedPostException.class)
    public String handleUndefinedPostException(UndefinedPostException e, Model model) {
        model.addAttribute(ERROR_MODEL_NAME, e.getMessage());
        return ERROR_PAGE_LINK;
    }

    @ExceptionHandler(UndefinedCookieException.class)
    public String handleUndefinedCookieException(UndefinedCookieException e, Model model) {
        model.addAttribute(ERROR_MODEL_NAME, e.getMessage());
        return ERROR_PAGE_LINK;
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public String handleObjectNotFoundException(ObjectNotFoundException e, Model model) {
        model.addAttribute(ERROR_MODEL_NAME, e.getMessage());
        return ERROR_PAGE_LINK;
    }

    @ExceptionHandler(LogoutUserException.class)
    public String handleLogoutUserException(LogoutUserException e, Model model) {
        model.addAttribute(ERROR_MODEL_NAME, e.getMessage());
        return ERROR_PAGE_LINK;
    }
}
