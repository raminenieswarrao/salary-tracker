package com.eswar.salarytracker.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, DataIntegrityViolationException.class})
    public String handleKnownErrors(Exception ex, Model model) {
        model.addAttribute("message", ex instanceof DataIntegrityViolationException
                ? "The requested change could not be saved because it conflicts with existing data."
                : ex.getMessage());
        return "error";
    }
}
