package com.logic.jobportal.controller;

import com.logic.jobportal.entity.JobSeekerProfile;
import com.logic.jobportal.entity.Skills;
import com.logic.jobportal.entity.Users;
import com.logic.jobportal.services.JobSeekerProfileService;
import com.logic.jobportal.services.UsersService;
import com.logic.jobportal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/job-seeker-profile")
public class JobSeekerProfileController {

    private final UsersService usersService;
    private final JobSeekerProfileService jobSeekerProfileService;

    @Autowired
    public JobSeekerProfileController(UsersService usersService, JobSeekerProfileService jobSeekerProfileService){
        this.usersService = usersService;
        this.jobSeekerProfileService = jobSeekerProfileService;
    }


    @GetMapping("/")
    public String jobSeekerProfile(Model model) {
        JobSeekerProfile jobSeekerProfile = new JobSeekerProfile();
        Users currentUser = usersService.getCurrentUser();
        Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileService.getOne(currentUser.getUserId());
        List<Skills> skills = new ArrayList<>();
        if(seekerProfile.isPresent()){
            jobSeekerProfile = seekerProfile.get();
            if(jobSeekerProfile.getSkills().isEmpty()){
                skills.add(new Skills());
                jobSeekerProfile.setSkills(skills);
            }
        }
        model.addAttribute("profile", jobSeekerProfile);
        model.addAttribute("skills", skills);
        return "job-seeker-profile";
    }

    @PostMapping("/addNew")
    public String addNew(JobSeekerProfile jobSeekerProfile, Model model, @RequestParam("pdf") MultipartFile resume,
                         @RequestParam("image") MultipartFile image){
        Users user = usersService.getCurrentUser();
        jobSeekerProfile.setUserId(user);
        jobSeekerProfile.setUserAccountId(user.getUserId());

        String uploadDir = "photos/candidates/" + jobSeekerProfile.getUserAccountId();

        String imageFileName = "";
        String resumeFileName = "";
        if(!Objects.requireNonNull(image.getOriginalFilename()).isEmpty()){
            imageFileName = StringUtils.cleanPath(image.getOriginalFilename());
            jobSeekerProfile.setProfilePhoto(imageFileName);
        }

        if(!Objects.requireNonNull(resume.getOriginalFilename()).isEmpty()){
            resumeFileName = StringUtils.cleanPath(resume.getOriginalFilename());
            jobSeekerProfile.setResume(resumeFileName);
        }

        try {
            if(!imageFileName.isEmpty())
                FileUploadUtil.saveFile(uploadDir, imageFileName, image);
            if(!resumeFileName.isEmpty())
                FileUploadUtil.saveFile(uploadDir, resumeFileName, resume);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Skills> skillsList = new ArrayList<>();
        for(Skills skill : jobSeekerProfile.getSkills()){
            skill.setJobSeekerProfile(jobSeekerProfile);
        }

        JobSeekerProfile savedProfile = jobSeekerProfileService.addNew(jobSeekerProfile);
        model.addAttribute("profile", savedProfile);
        model.addAttribute("skills", skillsList);
        return "redirect:/dashboard/";
    }

}
