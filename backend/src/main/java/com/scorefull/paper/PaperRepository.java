package com.scorefull.paper;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Paper, Long> {

    List<Paper> findBySubjectId(Long subjectId);
}