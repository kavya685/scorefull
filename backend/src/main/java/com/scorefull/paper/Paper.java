package com.scorefull.paper;

import com.scorefull.academic.subject.Subject;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int examYear;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    private boolean frequentlyAsked;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    private String originalFileName;

    private String storedFileName;
}