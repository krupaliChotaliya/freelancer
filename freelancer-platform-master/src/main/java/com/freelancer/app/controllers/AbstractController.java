package com.freelancer.app.controllers;

import com.freelancer.app.models.User;
import com.freelancer.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public abstract class AbstractController {

    @Autowired
    UserService userService;
    /**
     * Get logged user
     *
     * @return models.com.freelancer.app.User
     **/
    protected User getCurrentUser(){

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails == false) {
            return null;
        }
        String username = ((UserDetails) principal).getUsername();

        return userService.getByEmail(username);
    }
}
