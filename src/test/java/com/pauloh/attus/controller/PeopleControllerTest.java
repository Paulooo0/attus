package com.pauloh.attus.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.pauloh.attus.TestAttusApplication;
import com.pauloh.attus.model.People;
import com.pauloh.attus.service.PeopleService;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest(classes = TestAttusApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeopleControllerTest {
  
  @LocalServerPort
  private Integer port;

  static Dotenv dotenv = Dotenv.load();
  
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
    "postgres:" + dotenv.get("POSTGRES_VERSION")
  );

  @BeforeAll
  static void beforeAll() {
    postgres.start();
  }

  @AfterAll
  static void afterAll() {
    postgres.close();
  }


  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private PeopleService peopleService;

  @Autowired
  private PeopleController peopleController;

  private People createPeople(String fullName, LocalDate birthDate) {
    People people = new People();
    
    people.setFullName(fullName);
    people.setBirthDate(birthDate);

    peopleService.createPeople(people);

    return people;
  }

  @Test
  void testCreatePeople() {
    People people = new People();
    people.setFullName("Aaa Aaa");
    people.setBirthDate(LocalDate.of(2000, 1, 1));

    ResponseEntity<People> response = peopleController.createPeople(people);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(people, response.getBody());
  }

  @Test
  void testDeletePeople() {
    People people = createPeople("Bbb Bbb", LocalDate.of(2000, 1, 1));

    ResponseEntity<String> response = peopleController.deletePeople(people.getId());

    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

    assertEquals("Pessoa de ID " + people.getId() + " deletada", response.getBody());
  }

  @Test
  void testFindByFullNameAndBirthDate() throws InterruptedException, ExecutionException {
    createPeople("Ccc Ccc", LocalDate.of(2000, 1, 1));

    CompletableFuture<ResponseEntity<People>> response = peopleController
      .findByFullNameAndBirthDate("Ccc Ccc", LocalDate.of(2000, 1, 1));

    assertEquals(HttpStatus.OK, response.get().getStatusCode());
  }


  @Test
  void testFindPeopleById() throws InterruptedException, ExecutionException {
    People people = createPeople("Ddd Ddd", LocalDate.of(2000, 1, 1));
    
    CompletableFuture<ResponseEntity<People>> response = peopleController
      .findPeopleById(people.getId());

    assertEquals(HttpStatus.OK, response.get().getStatusCode());
  }


  @Test
  void testUpdatePeople() {
    People people = createPeople("Fff Fff", LocalDate.of(2000, 1, 1));

    People newPeople = people;
    newPeople.setFullName("Ggg Ggg");

    ResponseEntity<People> response = peopleController
      .updatePeople(people.getId(), newPeople);

    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals("Ggg Ggg", people.getFullName());
  }
}
