package com.jobboard.jobboard.module.candidature;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class InterviewRequest {
    private LocalDate interviewDate;
    private LocalTime interviewTime;
    private String location;
    private String interviewer;
    private String notes;
}
