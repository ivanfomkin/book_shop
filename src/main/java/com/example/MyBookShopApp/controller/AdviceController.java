package com.example.MyBookShopApp.controller;

import com.example.MyBookShopApp.exception.EmptySearchQueryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@ControllerAdvice
public class AdviceController {
    @ExceptionHandler(EmptySearchQueryException.class)
    public String handleEmptySearchQueryException(EmptySearchQueryException exception, RedirectAttributes redirectAttributes) {
        log.error(exception.getMessage());
        redirectAttributes.addFlashAttribute("searchError", exception);
        return "redirect:/";
    }
}
