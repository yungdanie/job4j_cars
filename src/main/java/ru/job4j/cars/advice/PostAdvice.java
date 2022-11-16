package ru.job4j.cars.advice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.job4j.cars.exception.UndefinedPostException;

@ControllerAdvice
public class PostAdvice {

    private final String errorPageLink;

    private final String errorModelName;

    public PostAdvice(@Value("ERROR_PAGE_LINK") String errorPageLink,
                      @Value("ERROR_MODEL_NAME") String errorModelName) {
        this.errorModelName = errorModelName;
        this.errorPageLink = errorPageLink;
    }

    @ExceptionHandler(UndefinedPostException.class)
    public String handleUndefinedPostException(UndefinedPostException e, Model model) {
        model.addAttribute(errorPageLink, e.getMessage());
        return errorModelName;
    }
}
