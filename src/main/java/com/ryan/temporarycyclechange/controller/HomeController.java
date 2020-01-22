package com.ryan.temporarycyclechange.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.ryan.temporarycyclechange.security.userdetails.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller that handles page views.
 * 
 * @author rsapl00
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String homePage(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("user", user);

        return "home";
    }

    @GetMapping("/access-denied")
    public String accessDenied(HttpServletRequest request, HttpServletResponse response, Model model) {

        response.setStatus(HttpStatus.FORBIDDEN.value());

        return "access-denied";
    }

    @GetMapping("/history")
    public String history(HttpServletRequest request, HttpServletResponse response, Model model) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("user", user);

        return "history";
    }

    @GetMapping("/login")
    public String logout(HttpServletRequest request, HttpServletResponse response, Model model) {
        return "redirect:/";
    }
}