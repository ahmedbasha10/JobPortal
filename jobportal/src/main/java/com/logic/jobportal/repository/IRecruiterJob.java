package com.logic.jobportal.repository;

import com.logic.jobportal.entity.JobCompany;
import com.logic.jobportal.entity.JobLocation;

public interface IRecruiterJob {

    Long getTotalCandidates();
    int getJob_post_id();
    String getJob_title();
    int getLocationId();
    String getCity();
    String getState();
    String getCountry();
    int getCompanyId();
    String getName();
}
