package com.scorefull.paper;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PaperRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must be 150 characters or fewer")
        String title,

        @Min(value = 1990, message = "Exam year must be 1990 or later")
        int examYear,

        @NotNull(message = "Subject is required")
        Long subjectId,

        @NotNull(message = "Difficulty is required")
        Difficulty difficulty,

        boolean frequentlyAsked) {
}