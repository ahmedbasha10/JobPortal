package com.logic.jobportal.repository;

import com.logic.jobportal.entity.JobPostActivity;
import com.logic.jobportal.entity.JobSeekerProfile;
import com.logic.jobportal.entity.JobSeekerSave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerSaveRepository extends JpaRepository<JobSeekerSave, Integer> {

    List<JobSeekerSave> findByUserId(JobSeekerProfile userId);

    List<JobSeekerSave> findByJob(JobPostActivity job);

}
