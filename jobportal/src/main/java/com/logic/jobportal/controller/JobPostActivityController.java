package com.logic.jobportal.controller;

import com.logic.jobportal.entity.*;
import com.logic.jobportal.services.JobPostActivityService;
import com.logic.jobportal.services.JobSeekerApplyService;
import com.logic.jobportal.services.JobSeekerSaveService;
import com.logic.jobportal.services.UsersService;
import com.logic.jobportal.util.ConstantsUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class JobPostActivityController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;

    @Autowired
    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService,
                                     JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService){
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
    }

    @GetMapping("/dashboard/")
    public String searchJobs(Model model,
                             @RequestParam(value = "job", required = false) String job,
                             @RequestParam(value = "location",required = false) String location,
                             @RequestParam(value = "partTime",required = false) String partTime,
                             @RequestParam(value = "fullTime",required = false) String fullTime,
                             @RequestParam(value = "freelance",required = false) String freelance,
                             @RequestParam(value = "remoteOnly",required = false) String remoteOnly,
                             @RequestParam(value = "officeOnly",required = false) String officeOnly,
                             @RequestParam(value = "partialOnly",required = false) String partialRemote,
                             @RequestParam(value = "today",required = false) boolean today,
                             @RequestParam(value = "days7",required = false) boolean days7,
                             @RequestParam(value = "days30",required = false) boolean days30){

        model.addAttribute("partTime", Objects.equals(partTime, ConstantsUtilities.PART_TIME));
        model.addAttribute("fullTime", Objects.equals(fullTime, ConstantsUtilities.FULL_TIME));
        model.addAttribute("freelance", Objects.equals(freelance, ConstantsUtilities.FREELANCE));

        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, ConstantsUtilities.REMOTE_ONLY));
        model.addAttribute("officeOnly", Objects.equals(officeOnly ,ConstantsUtilities.OFFICE_ONLY));
        model.addAttribute("partialRemote", Objects.equals(partialRemote, ConstantsUtilities.PARTIAL_REMOTE));

        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);

        model.addAttribute("job", job);
        model.addAttribute("partTime", location);

        LocalDate searchDate = null;
        List<JobPostActivity> jobPost = null;
        boolean dateSearchFlag = true;
        boolean remote = true;
        boolean type = true;

        if(days30){
            searchDate = LocalDate.now().minusDays(30);
        } else if(days7){
            searchDate = LocalDate.now().minusDays(7);
        } else if(today){
            searchDate = LocalDate.now();
        } else {
            dateSearchFlag = false;
        }

        if(partTime == null && fullTime == null && freelance == null){
            partTime = ConstantsUtilities.PART_TIME;
            fullTime = ConstantsUtilities.FULL_TIME;
            freelance = ConstantsUtilities.FREELANCE;
            remote = false;
        }

        if(officeOnly == null && remoteOnly == null & partialRemote == null){
            remoteOnly = ConstantsUtilities.REMOTE_ONLY;
            officeOnly = ConstantsUtilities.OFFICE_ONLY;
            partialRemote = ConstantsUtilities.PARTIAL_REMOTE;
            type = false;
        }

        if(!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && StringUtils.hasText(location)){
            jobPost = jobPostActivityService.getAll();
        } else {
            System.out.println("job -> " + job);
            System.out.println("location -> " + location);
            System.out.println("list1 -> " + Arrays.asList(partTime, fullTime, freelance));
            System.out.println("list2 -> " + Arrays.asList(remoteOnly, officeOnly, partialRemote));
            System.out.println("searchDate -> " + searchDate);
            jobPost = jobPostActivityService.search(job, location, Arrays.asList(remoteOnly, officeOnly, partialRemote),
                    Arrays.asList(partTime, fullTime, freelance) ,searchDate);
            System.out.println("jobPost -> " + jobPost);
        }

        Object currentUserProfile = usersService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            model.addAttribute("username", authentication.getName());
            if(authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("Recruiter"))){
                List<RecruiterJobsDto> recruiterJobs = jobPostActivityService.getRecruiterJobs(((RecruiterProfile)currentUserProfile).getUserAccountId());
                model.addAttribute("jobPost", recruiterJobs);
            } else {
                List<JobSeekerApply> jobSeekerApplies = jobSeekerApplyService.getCandidatesJobs((JobSeekerProfile) currentUserProfile);
                List<JobSeekerSave> jobSeekerSaves = jobSeekerSaveService.getSavedJobsForCandidate((JobSeekerProfile) currentUserProfile);

                boolean exist, saved;
                for(JobPostActivity jobPostActivity : jobPost){
                    exist = false;
                    saved = false;
                    for (JobSeekerApply apply : jobSeekerApplies){
                        if(jobPostActivity.getJobPostId() == apply.getJob().getJobPostId()){
                            jobPostActivity.setIsActive(true);
                            exist = true;
                            break;
                        }
                    }
                    for (JobSeekerSave save : jobSeekerSaves){
                        if(jobPostActivity.getJobPostId() == save.getJob().getJobPostId()){
                            jobPostActivity.setIsSaved(true);
                            saved = true;
                            break;
                        }
                    }
                    if(!exist){
                        jobPostActivity.setIsActive(false);
                    }
                    if(!saved){
                        jobPostActivity.setIsSaved(false);
                    }
                    model.addAttribute("jobPost",jobPost);
                }
            }
        }
//        model.addAttribute("jobPost", jobPost);
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
        JobPostActivity savedJob = jobPostActivityService.addNew(jobPostActivity);
        System.out.println("saved job -> " + savedJob);
        return "redirect:/dashboard/";
    }

    @PostMapping("dashboard/edit/{id}")
    public String editJob(@PathVariable("id") int id, Model model){
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity", jobPostActivity);
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }
}
