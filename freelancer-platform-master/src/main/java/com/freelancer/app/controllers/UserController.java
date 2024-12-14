package com.freelancer.app.controllers;

import com.freelancer.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/byskills")
    public ResponseEntity<String[]> getUsersBySkills(@RequestParam List<String> requiredSkills) {
        // Fetch users matching skills
        List<String> users = userService.findUsersBySkills(requiredSkills);
        String[] usersArray = users.toArray(new String[0]);
        return ResponseEntity.ok(usersArray);
    }
}

