package com.logic.jobportal.repository;

import com.logic.jobportal.entity.JobPostActivity;
import com.logic.jobportal.entity.JobSeekerApply;
import com.logic.jobportal.entity.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobSeekerApplyRepository extends JpaRepository<JobSeekerApply, Integer> {

    List<JobSeekerApply> findByUserId(JobSeekerProfile userId);

    List<JobSeekerApply> findByJob(JobPostActivity job);

}
