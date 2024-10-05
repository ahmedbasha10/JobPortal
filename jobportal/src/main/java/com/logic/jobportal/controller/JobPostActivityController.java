package com.logic.jobportal.controller;

import com.logic.jobportal.entity.JobPostActivity;
import com.logic.jobportal.entity.RecruiterProfile;
import com.logic.jobportal.entity.Users;
import com.logic.jobportal.services.JobPostActivityService;
import com.logic.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Date;

@Controller
public class JobPostActivityController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;

    @Autowired
    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService){
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
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

    @GetMapping("/dashboard/add")
    public String addNewJob(Model model){
        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("/dashboard/addNew")
    public String saveNewJob(JobPostActivity jobPostActivity, Model model){
        Users user = usersService.getCurrentUser();
        if(user != null){
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());
        model.addAttribute("jobPostActivity", jobPostActivity);
        System.out.println("jobPost Actibity- > " + jobPostActivity);
        JobPostActivity savedJob = jobPostActivityService.addNew(jobPostActivity);
        System.out.println("saved job -> " + savedJob);
        return "redirect:/dashboard/";
    }
}
