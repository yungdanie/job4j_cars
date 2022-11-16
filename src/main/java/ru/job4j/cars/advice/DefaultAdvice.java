package ru.job4j.cars.advice;

import org.hibernate.ObjectNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.job4j.cars.exception.CreateUuidException;
import ru.job4j.cars.exception.UndefinedCookieException;

@ControllerAdvice
public class DefaultAdvice {

    private final static String ERROR_PAGE_LINK = "state/errorPage";

    private final static String ERROR_MODEL_NAME = "errorMessage";

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

    @ExceptionHandler(CreateUuidException.class)
    public String handleObjectNotFoundException(CreateUuidException e, Model model) {
        model.addAttribute(ERROR_MODEL_NAME, e.getMessage());
        return ERROR_PAGE_LINK;
    }

}
