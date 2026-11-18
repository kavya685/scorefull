package com.scorefull.academic.subject;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubjectRequest(

        @NotBlank(message = "Subject name is required")
        String name,

        @Min(value = 1, message = "Semester must be between 1 and 8")
        @Max(value = 8, message = "Semester must be between 1 and 8")
        int semester,

        @NotNull(message = "Course is required")
        Long courseId) {
}