package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping( "/admin")
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminsController {

    private final UserService userService;
    private final RoleService roleService;
    private final RoleDao roleDao;

    @Autowired
    public AdminsController(UserService userService, RoleService roleService,
                            RoleDao roleDao) {
        this.userService = userService;
        this.roleService = roleService;
        this.roleDao = roleDao;
    }

    @GetMapping
    public String showUsers(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("usersList", userService.findAllUsers());
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("roles", userDetails.getAuthorities());
        return "admin";
    }

    @GetMapping("/user")
    public String showAdmin(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("user", userService.findUserByUsername(userDetails.getUsername()));
        model.addAttribute("userDetails", userDetails);
        return "user";
    }

    @GetMapping("/new")
    public String newUser(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("roles", roleService.findAllRoles());
        model.addAttribute("user", new User());
        return "/new";
    }

    @PostMapping ("/new")
    public String saveUser(@ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/new";
        }
        roleService.setUserRoles(user);
        userService.saveUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String edit(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable("id") Long id, Model model) {
        User user = userService.findUserById(id);
        model.addAttribute("userDetails", userDetails);
        model.addAttribute("roles", roleDao.findAll());
        model.addAttribute("user", user);
        return "edit";
    }

    @PutMapping ("/edit/{id}")
    public String updateUser(@ModelAttribute("user") User user, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/edit/{id}";
        }
        roleService.setUserRoles(user);
        userService.updateUser(user);
        return "redirect:/admin";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}
