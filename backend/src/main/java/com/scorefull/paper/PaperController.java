package com.scorefull.paper;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.scorefull.academic.subject.Subject;
import com.scorefull.academic.subject.SubjectRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/papers")
@RequiredArgsConstructor
public class PaperController {

    private final PaperRepository paperRepository;
    private final SubjectRepository subjectRepository;
    private final FileStorageService fileStorageService;

    @GetMapping
    public List<Paper> getAll(@RequestParam(required = false) Long subjectId) {
        if (subjectId != null) {
            return paperRepository.findBySubjectId(subjectId);
        }
        return paperRepository.findAll();
    }

    @PostMapping
    public Paper add(@Valid @RequestBody PaperRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Subject not found"));

        Paper paper = new Paper();
        paper.setTitle(request.title());
        paper.setExamYear(request.examYear());
        paper.setDifficulty(request.difficulty());
        paper.setFrequentlyAsked(request.frequentlyAsked());
        paper.setSubject(subject);

        return paperRepository.save(paper);
    }

    @PostMapping("/{id}/file")
    public Paper uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        Paper paper = findPaper(id);

        String storedName = fileStorageService.savePdf(file);
        paper.setStoredFileName(storedName);
        paper.setOriginalFileName(file.getOriginalFilename());

        return paperRepository.save(paper);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        Paper paper = findPaper(id);

        if (paper.getStoredFileName() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "This paper has no file yet");
        }

        Resource resource = fileStorageService.load(paper.getStoredFileName());
        String downloadName = paper.getOriginalFileName() != null
                ? paper.getOriginalFileName()
                : "paper-" + id + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(downloadName).build().toString())
                .body(resource);
    }

    private Paper findPaper(Long id) {
        return paperRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Paper not found"));
    }
}