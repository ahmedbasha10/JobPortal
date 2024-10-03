package com.logic.jobportal.controller;

import com.logic.jobportal.entity.RecruiterProfile;
import com.logic.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JobPostActivityController {

    private final UsersService usersService;

    @Autowired
    public JobPostActivityController(UsersService usersService){
        this.usersService = usersService;
    }

    @GetMapping("/dashboard/")
    public String searchJobs(Model model){
        RecruiterProfile currentUserProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            model.addAttribute("username", authentication.getName());
        }
        System.out.println("current profile -> " + currentUserProfile);
        model.addAttribute("user", currentUserProfile);
        return "dashboard";
    }

}
