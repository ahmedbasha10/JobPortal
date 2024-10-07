package com.logic.jobportal.controller;


import com.logic.jobportal.entity.RecruiterProfile;
import com.logic.jobportal.entity.Users;
import com.logic.jobportal.services.RecruiterProfileService;
import com.logic.jobportal.services.UsersService;
import com.logic.jobportal.util.FileUploadUtil;
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

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersService usersService;
    private final RecruiterProfileService recruiterProfileService;

    public RecruiterProfileController(UsersService usersService, RecruiterProfileService recruiterProfileService){
        this.usersService = usersService;
        this.recruiterProfileService = recruiterProfileService;
    }


    @GetMapping("/")
    public String recruiterProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();
            Users users = usersService.getByEmail(username).orElseThrow(() -> new UsernameNotFoundException("couldn't find user"));
            Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.findById(users.getUserId());

            recruiterProfile.ifPresent(profile -> model.addAttribute("profile", profile));
        }

        return "recruiter_profile";
    }

    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile, @RequestParam("image") MultipartFile file, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            String username = authentication.getName();
            Users users = usersService.getByEmail(username).orElseThrow(() -> new UsernameNotFoundException("couldn't find user"));
            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
        }
        model.addAttribute("profile", recruiterProfile);
        String fileName = "";
        if(!Objects.requireNonNull(file.getOriginalFilename()).isEmpty()){
            fileName = StringUtils.cleanPath(file.getOriginalFilename());
            recruiterProfile.setProfilePhoto(fileName);
        }
        RecruiterProfile savedProfile = recruiterProfileService.addNew(recruiterProfile);

        String uploadDir = "photos/recruiter/" + savedProfile.getUserAccountId();

        try {
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } catch (Exception e){
            e.printStackTrace();
        }

        return "redirect:/dashboard/";
    }
}
