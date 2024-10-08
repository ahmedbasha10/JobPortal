package com.logic.jobportal.services;

import com.logic.jobportal.entity.JobPostActivity;
import com.logic.jobportal.entity.JobSeekerProfile;
import com.logic.jobportal.entity.JobSeekerSave;
import com.logic.jobportal.repository.JobSeekerSaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.List;

@Service
public class JobSeekerSaveService {

    private final JobSeekerSaveRepository jobSeekerSaveRepository;

    @Autowired
    public JobSeekerSaveService(JobSeekerSaveRepository jobSeekerSaveRepository){
        this.jobSeekerSaveRepository = jobSeekerSaveRepository;
    }

    public List<JobSeekerSave> getSavedJobsForCandidate(JobSeekerProfile candidate){
        return jobSeekerSaveRepository.findByUserId(candidate);
    }

    public List<JobSeekerSave> getJobCandidates(JobPostActivity job){
        return jobSeekerSaveRepository.findByJob(job);
    }

    public JobSeekerSave addNew(JobSeekerSave jobSeekerSave){
        return jobSeekerSaveRepository.save(jobSeekerSave);
    }

}
