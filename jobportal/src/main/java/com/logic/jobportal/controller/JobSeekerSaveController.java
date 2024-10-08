package com.logic.jobportal.controller;

import com.logic.jobportal.entity.JobPostActivity;
import com.logic.jobportal.entity.JobSeekerProfile;
import com.logic.jobportal.entity.JobSeekerSave;
import com.logic.jobportal.entity.Users;
import com.logic.jobportal.services.JobPostActivityService;
import com.logic.jobportal.services.JobSeekerProfileService;
import com.logic.jobportal.services.JobSeekerSaveService;
import com.logic.jobportal.services.UsersService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class JobSeekerSaveController {

    private final UsersService usersService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerSaveService jobSeekerSaveService;

    public JobSeekerSaveController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService, JobPostActivityService jobPostActivityService, JobSeekerSaveService jobSeekerSaveService) {
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @PostMapping("job-details/save/{id}")
    public String save(@PathVariable("id") int id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();
            Users currentUser = usersService.findByEmail(username);
            Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(currentUser.getUserId());
            JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
            JobSeekerSave jobSeekerSave;
            if(seekerProfile.isPresent() && jobPostActivity != null){
                jobSeekerSave = new JobSeekerSave();
                jobSeekerSave.setUserId(seekerProfile.get());
                jobSeekerSave.setJob(jobPostActivity);
            } else {
                throw new RuntimeException("User not found");
            }
            jobSeekerSaveService.addNew(jobSeekerSave);
        }
        return "redirect:/dashboard/";
    }

    @GetMapping("saved-jobs/")
    public String savedJobs(Model model){
        List<JobPostActivity> savedJobs = new ArrayList<>();
        Object currentProfile = usersService.getCurrentUserProfile();
        List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getSavedJobsForCandidate((JobSeekerProfile) currentProfile);
        for(JobSeekerSave jobSeekerSave : jobSeekerSaveList){
            savedJobs.add(jobSeekerSave.getJob());
        }
        model.addAttribute("jobPost", savedJobs);
        model.addAttribute("user", currentProfile);
        return "saved-jobs";
    }
}
