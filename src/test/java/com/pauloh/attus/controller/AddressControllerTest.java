package com.pauloh.attus.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.List;
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

import com.pauloh.attus.model.Address;
import com.pauloh.attus.model.People;
import com.pauloh.attus.service.AddressService;
import com.pauloh.attus.service.PeopleService;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddressControllerTest {

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
  private AddressController addressController;

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
  void testCreateAddress() {
    People people = createPeople("Aaa Aaa", LocalDate.of(2000, 1, 1));
    Address address = new Address();
    address.setPeopleId(people.getId());
    address.setStreetAddress("Aaa Aaa");
    address.setCep(1111111);
    address.setNumber(1);
    address.setCity("Aaa Aaa");
    address.setState("Aaa Aaa");

    ResponseEntity<Address> response = addressController.createAddress(people.getId(), address);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(address, response.getBody());
  }

  @Test
  void testDeleteAddressById() {
    People people = createPeople("Bbb Bbb", LocalDate.of(2000, 1, 1));
    Address address = createAddress(people.getId(),
    "Bbb Bbb",
    1111111,
    1,
    "Bbb Bbb",
    "Bbb Bbb"
    );

    ResponseEntity<String> response = addressController.deleteAddressById(address.getId());

    // Assert
    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals("Endereço de ID " + address.getId() + " deletado", response.getBody());

    // verify(addressService).deleteAddressById(address.getId());
  }

  @Test
  void testFindAddressById() throws InterruptedException, ExecutionException {
    People people = createPeople("Ccc Ccc", LocalDate.of(2000, 1, 1));
    Address address = createAddress(people.getId(),
    "Ccc Ccc",
    1111111,
    1,
    "Ccc Ccc",
    "Ccc Ccc"
    );
    
    // when(addressService.findAddressById(address.getId())).thenReturn(address);

    CompletableFuture<ResponseEntity<Address>> response = addressController.findAddressById(address.getId());

    assertEquals(HttpStatus.OK, response.get().getStatusCode());
  }

  @SuppressWarnings("null")
  @Test
  void testFindAllAddresses() throws InterruptedException, ExecutionException {
    People people = createPeople("Ddd Ddd", LocalDate.of(2000, 1, 1));
    createAddress(people.getId(),
    "Ddd Ddd",
    1111111,
    1,
    "Ddd Ddd",
    "Ddd Ddd"
    );

    // Act
    CompletableFuture<ResponseEntity<List<Address>>> response = addressController.findAllAddresses();

    // Assert
    assertEquals(HttpStatus.OK, response.get().getStatusCode());
    assertEquals(4, response.get().getBody().size());
  }

  @Test
  void testUpdateAddress() {
    People people = createPeople("Eee Eee", LocalDate.of(2000, 1, 1));
    Address address = createAddress(people.getId(),
    "Eee Eee",
    1111111,
    1,
    "Eee Eee",
    "Eee Eee"
    );

    Address updatedAddress = new Address();
    updatedAddress.setId(address.getId());
    updatedAddress.setStreetAddress("Fff Fff");
    updatedAddress.setCep(1111111);
    updatedAddress.setNumber(1);
    updatedAddress.setCity("Fff Fff");
    updatedAddress.setState("Fff Fff");

    ResponseEntity<Address> response = addressController.updateAddress(address.getId(), updatedAddress);

    assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    assertEquals(updatedAddress, response.getBody());
  }
}
