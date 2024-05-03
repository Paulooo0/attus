package com.pauloh.attus.service;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

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
public class AddressServiceTest {

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
  void testCreateAddress() throws InterruptedException, ExecutionException {
    People people = createPeople("Aaa Aaa", LocalDate.of(2000, 1, 1));
    Address address = createAddress(people.getId(),
      "Aaa Aaa",
      1111111,
      1,
      "Aaa Aaa",
      "Aaa Aaa");

    assertEquals(address.getId(), addressService.findAddressById(address.getId()).getId());
    
    assertThrowsExactly(RuntimeException.class, () -> createAddress(people.getId(),
      "Aaa Aaa",
      1111111,
      1,
      "Aaa Aaa",
      "Aaa Aaa"));

      Address address2 = createAddress(people.getId(),
      "Aaaa Aaaa",
      1111110,
      1,
      "Aaaa Aaaa",
      "Aaaa Aaaa");

    assertEquals(AddressCategory.ALTERNATIVE, addressService.findAddressById(address2.getId()).getCategory());
  }

  @Test
  void testDeleteAddressById() throws InterruptedException, ExecutionException {
    People people = createPeople("Bbb Bbb", LocalDate.of(2000, 1, 1));
    Address address = createAddress(people.getId(),
      "Bbb Bbb",
      2222222,
      1,
      "Bbb Bbb",
      "Bbb Bbb");

    addressService.deleteAddressById(address.getId());

    assertThrowsExactly(RuntimeException.class, () -> addressService.findAddressById(address.getId()).getId());

    Address address2 = createAddress(people.getId(),
      "Bbb Bbb",
      2222220,
      1,
      "Bbb Bbb",
      "Bbb Bbb");

      Address address3 = createAddress(people.getId(),
      "Bbbb Bbbb",
      2222221,
      1,
      "Bbb Bbb",
      "Bbb Bbb");

    addressService.deleteAddressById(address2.getId());
    assertEquals(AddressCategory.MAIN, addressService.findAddressById(address3.getId()).getCategory());
  }

  @Test
  void testFindAddressById() throws InterruptedException, ExecutionException {
    People people = createPeople("Ccc Ccc", LocalDate.of(2000, 1, 1));
    Address address = createAddress(people.getId(),
      "Ccc Ccc",
      3333333,
      1,
      "Ccc Ccc",
      "Ccc Ccc");

    assertEquals(address.getId(), addressService.findAddressById(address.getId()).getId());
  }

  @Test
  void testFindAllAddresses() throws InterruptedException, ExecutionException {
    People people = createPeople("Ddd Ddd", LocalDate.of(2000, 1, 1));
    createAddress(people.getId(),
      "Ddd Ddd",
      4444444,
      1,
      "Ddd Ddd",
      "Ddd Ddd");
    
    List<Address> addresses = addressService.findAllAddresses();

    // Aaa Aaa, Aaaa Aaaa, Bbb Bbb, Bbbb Bbbb, Ccc Ccc, Ddd Ddd
    assertEquals(6, addresses.size());
  }

  @Test
  void testUpdateAddress() throws InterruptedException, ExecutionException {
    People people = createPeople("Eee Eee", LocalDate.of(2000, 1, 1));
    Address address = createAddress(people.getId(),
      "Eee Eee",
      5555555,
      1,
      "Eee Eee",
      "Eee Eee");

    Address updatedAddress = new Address(
      "Fff Fff",
      6666666,
      2,
      "City",
      "State"
    );

    addressService.updateAddress(address.getId(), updatedAddress);
    assertEquals(updatedAddress.getStreetAddress(), addressService.findAddressById(address.getId()).getStreetAddress());
    
    Address updatedAddress2 = new Address(
      "Ggg Ggg",
      7777777,
      2,
      "City",
      "State",
      AddressCategory.ALTERNATIVE
    );

    addressService.updateAddress(address.getId(), updatedAddress2);
    assertEquals(updatedAddress2.getStreetAddress(), addressService.findAddressById(address.getId()).getStreetAddress());

    Address updatedAddress3 = new Address(
      "Hhh Hhh",
      8888888,
      2,
      "City",
      "State",
      AddressCategory.MAIN
    );

    addressService.updateAddress(address.getId(), updatedAddress3);
    assertEquals(AddressCategory.MAIN, addressService.findAddressById(address.getId()).getCategory());
  }
}
