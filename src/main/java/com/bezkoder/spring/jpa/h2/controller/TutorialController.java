package com.bezkoder.spring.jpa.h2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import com.bezkoder.spring.jpa.h2.model.Tutorial;
import com.bezkoder.spring.jpa.h2.repository.TutorialRepository;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TutorialController {

  private final TutorialRepository tutorialRepository;

  public TutorialController(@NonNull TutorialRepository tutorialRepository) {
    this.tutorialRepository = tutorialRepository;
  }

  @GetMapping("/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorials(
      @RequestParam(required = false) @Nullable String title) {

    List<Tutorial> tutorials = new ArrayList<>();

    if (title == null) {
      tutorialRepository.findAll().forEach(tutorials::add);
    } else {
      tutorialRepository.findByTitleContainingIgnoreCase(title).forEach(tutorials::add);
    }

    if (tutorials.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(tutorials, HttpStatus.OK);
  }

  @GetMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> getTutorialById(@PathVariable long id) {
    Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

    if (tutorialData.isPresent()) {
      return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @PostMapping("/tutorials")
  public ResponseEntity<Tutorial> createTutorial(@RequestBody @NonNull Tutorial tutorial) {
    Tutorial savedTutorial = tutorialRepository.save(
        new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false)
    );
    return new ResponseEntity<>(savedTutorial, HttpStatus.CREATED);
  }

  @PutMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> updateTutorial(
      @PathVariable long id,
      @RequestBody @NonNull Tutorial tutorial) {

    Optional<Tutorial> tutorialData = tutorialRepository.findById(id);

    if (tutorialData.isPresent()) {
      Tutorial updatedTutorial = tutorialData.get();
      updatedTutorial.setTitle(tutorial.getTitle());
      updatedTutorial.setDescription(tutorial.getDescription());
      updatedTutorial.setPublished(tutorial.isPublished());
      return new ResponseEntity<>(tutorialRepository.save(updatedTutorial), HttpStatus.OK);
    }

    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @DeleteMapping("/tutorials/{id}")
  public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable long id) {
    if (!tutorialRepository.existsById(id)) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    tutorialRepository.deleteById(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/tutorials")
  public ResponseEntity<HttpStatus> deleteAllTutorials() {
    tutorialRepository.deleteAll();
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/tutorials/published")
  public ResponseEntity<List<Tutorial>> findByPublished() {
    List<Tutorial> tutorials = tutorialRepository.findByPublished(true);

    if (tutorials.isEmpty()) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    return new ResponseEntity<>(tutorials, HttpStatus.OK);
  }
}