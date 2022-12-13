package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/")
@PreAuthorize("hasAuthority('USER')")
public class UsersController {

    private final UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("login")
    public String login(Model model) {
        return "login";
    }

    @GetMapping("user")
    public String getCurrentUser(@AuthenticationPrincipal UserDetails user, Model model) {
        model.addAttribute("user", userService.findUserByUsername(user.getUsername()));
        model.addAttribute("concurrent_user", user);
        return "user";
    }
}
