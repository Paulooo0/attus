package com.pauloh.attus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pauloh.attus.model.People;
import com.pauloh.attus.service.PeopleService;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/people")
public class PeopleController {

  private final PeopleService peopleService;
  
  public PeopleController(PeopleService peopleService) {
    this.peopleService = peopleService;
  }
  
  @PostMapping()
  public ResponseEntity<People> createPeople(@RequestBody People people) {
    peopleService.createPeople(people);
    return ResponseEntity.status(HttpStatus.CREATED).body(people);
  }
  
  @Async
  @GetMapping("/id={id}")
  public CompletableFuture<ResponseEntity<People>> findPeopleById(@PathVariable Long id) {
    return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.OK).body(peopleService.findPeopleById(id)));
  }
  
  @Async
  @GetMapping("/name={name}/birthDate={birthDate}")
  public CompletableFuture<ResponseEntity<People>> findByFullNameAndBirthDate(
    @PathVariable String name,
    @PathVariable LocalDate birthDate
    ) {
    return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.OK).body(peopleService.findByFullNameAndBirthDate(name, birthDate)));
  }
  
  @PutMapping("/id={id}")
  public ResponseEntity<People> updatePeople(@PathVariable Long id, @RequestBody People people) {
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(peopleService.updatePeople(id, people));
  }
  
  @DeleteMapping("/id={id}")
  public ResponseEntity<String> deletePeople(@PathVariable Long id) {
    peopleService.deletePeopleById(id);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body("Pessoa de ID " + id + " deletada");
  }
}
