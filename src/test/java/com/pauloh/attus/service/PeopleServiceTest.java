package com.pauloh.attus.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import com.pauloh.attus.model.Address;
import com.pauloh.attus.model.People;
import com.pauloh.attus.model.enums.AddressCategory;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PeopleServiceTest {

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
  private AddressService addressService;

  private People createPeople(String fullName, LocalDate birthDate) {
    People people = new People();
    
    people.setFullName(fullName);
    people.setBirthDate(birthDate);

    peopleService.createPeople(people);

    return people;
  }

  private Address createAddress(Long id, String streetAddress, Integer cep, Integer number, String city, String state) {
    Address address = new Address(
      streetAddress,
      cep,
      number,
      city,
      state
      );

    addressService.createAddress(id, address);
    return address;
  }


  @Test
  void testCreatePeople() throws InterruptedException, ExecutionException {
    People people = createPeople("Aaa Aaa", LocalDate.of(2000, 1, 1));
    Address address = createAddress(people.getId(),
    "Aaa Aaa",
    1111111,
    1,
    "Aaa Aaa",
    "Aaa Aaa");

    People findedPeople = peopleService.findPeopleById(people.getId());
    Address findedAddress = addressService.findAddressById(address.getId());
    Thread.sleep(1000);
    
    assertEquals(people.getId(), findedPeople.getId());
    assertEquals(address.getId(), findedAddress.getId());

    assertThrowsExactly(IllegalArgumentException.class, () -> createPeople("Aaa", LocalDate.of(2000, 1, 1)));
    assertThrowsExactly(IllegalArgumentException.class, () -> createPeople("", LocalDate.of(2000, 1, 1)));
    
    List<People> peopleList = peopleService.findAllPeople();
    boolean found = false;
    for (People p : peopleList) {
      if (p.getFullName().equals(people.getFullName())
        && p.getBirthDate().equals(people.getBirthDate())) {
        found = true;
        break;
      }
    }
    assertTrue(found, "Pessoa já existe");
  }

  @Test
  void testDeletePeopleById() throws InterruptedException, ExecutionException {
    People people = createPeople("Bbb Bbb", LocalDate.of(2000, 1, 1));
    createAddress(people.getId(),
      "Ccc Ccc",
      2222222,
      1,
      "Ccc Ccc",
      "Ccc Ccc");

    addressService.deleteAddressById(people.getId());
    
    peopleService.deletePeopleById(people.getId());

    assertThrowsExactly(RuntimeException.class, () -> peopleService.findPeopleById(people.getId()));
  }

  @Test
  void testFindPeopleById() throws InterruptedException, ExecutionException {
    People people = createPeople("Ddd Ddd", LocalDate.of(2000, 1, 1));
    Thread.sleep(1000);
    People findedPeople = peopleService.findPeopleById(people.getId());
    assertEquals(people.getId(), findedPeople.getId());

    assertThrowsExactly(RuntimeException.class, () -> peopleService.findPeopleById(-1L));
  }



  @Test
  void testUpdatePeople() throws InterruptedException, ExecutionException {
    People people = createPeople("Eee Eee", LocalDate.of(2000, 1, 1));
    Address firstAddress = createAddress(people.getId(),
      "Aaa Aaa",
      3333333,
      1,
      "Aaa Aaa",
      "Aaa Aaa"
      );

    People updatedPeople = new People();
    updatedPeople.setFullName("Fff Fff");

    peopleService.updatePeople(people.getId(), updatedPeople);

    assertEquals("Fff Fff", peopleService.findPeopleById(people.getId()).getFullName());

    Address newMainAddress = new Address(
      "New Main Address",
      1234567,
      2,
      "City",
      "State",
      AddressCategory.MAIN
    );

    addressService.createAddress(people.getId(), newMainAddress);

    Address updatedNewMainAddress = addressService.findAddressById(newMainAddress.getId());
    assertEquals("New Main Address", updatedNewMainAddress.getStreetAddress());
    assertEquals(AddressCategory.MAIN, updatedNewMainAddress.getCategory());

    Address updatedFirstAddress = addressService.findAddressById(firstAddress.getId());
    assertEquals(AddressCategory.ALTERNATIVE, updatedFirstAddress.getCategory());
  }

  @Test
  void testFindByFullNameAndBirthDate() {
    People people = createPeople("Ggg Ggg", LocalDate.of(2000, 1, 1));

    People foundPerson = peopleService.findByFullNameAndBirthDate("Ggg Ggg", LocalDate.of(2000, 1, 1));

    assertEquals(people.getId(), foundPerson.getId());

    assertThrowsExactly(RuntimeException.class, () -> peopleService.findByFullNameAndBirthDate("Ggg Ggg", LocalDate.of(2001, 1, 1)));
  }
}
