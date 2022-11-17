package ru.job4j.cars.advice;

import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.job4j.cars.exception.CreateUuidException;
import ru.job4j.cars.exception.UndefinedCookieException;

@ControllerAdvice
public class DefaultAdvice {

    private final String errorPageLink;

    private final String errorModelName;

    public DefaultAdvice(@Value("${ERROR_PAGE_LINK}") String errorPageLink,
                         @Value("${ERROR_MODEL_NAME}") String errorModelName) {
        this.errorPageLink = errorPageLink;
        this.errorModelName = errorModelName;
    }

    @ExceptionHandler(UndefinedCookieException.class)
    public String handleUndefinedCookieException(UndefinedCookieException e, Model model) {
        model.addAttribute(errorModelName, e.getMessage());
        return errorPageLink;
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public String handleObjectNotFoundException(ObjectNotFoundException e, Model model) {
        model.addAttribute(errorModelName, e.getMessage());
        return errorPageLink;
    }

    @ExceptionHandler(CreateUuidException.class)
    public String handleObjectNotFoundException(CreateUuidException e, Model model) {
        model.addAttribute(errorModelName, e.getMessage());
        return errorPageLink;
    }

}
