package com.pauloh.attus.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

import com.pauloh.attus.model.Address;
import com.pauloh.attus.model.People;
import com.pauloh.attus.repository.AddressRepository;
import com.pauloh.attus.repository.PeopleRepository;

@Service
public class PeopleService {
  private final PeopleRepository peopleRepository;
  private final AddressRepository addressRepository;
  
  public PeopleService(
    PeopleRepository peopleRepository,
    AddressRepository addressRepository
    ) {
    this.peopleRepository = peopleRepository;
    this.addressRepository = addressRepository;
  }

  public void createPeople(People people) {
    List<People> peopleList = peopleRepository.findAll();
    for (People p : peopleList) {
      if (p.getFullName().equals(people.getFullName())
        && p.getBirthDate().equals(people.getBirthDate())) {
        throw new RuntimeException("Pessoa já existe");
      }
    }

    peopleRepository.save(people);
  }

  public List<People> findAllPeople() {    
    return peopleRepository.findAll();
  }
  
  public People findPeopleById(Long id) {
    return peopleRepository.findById(id).orElseThrow(
      () -> new RuntimeException("Pessoa não encontrada com o ID: " + id));
  }

  public People findByFullNameAndBirthDate(String fullName, LocalDate birthDate) {
    People foundPerson = null;
    for (People p : peopleRepository.findAll()) {
        if (p.getFullName().equals(fullName) && p.getBirthDate().equals(birthDate)) {
            foundPerson = p;
            break;
        }
    }
    
    if (foundPerson == null) {
        throw new RuntimeException("Pessoa não encontrada");
    }
    
    return foundPerson;
  }
  
  public People updatePeople(Long id, People people) {
    People existingPeople = peopleRepository.findById(id).orElseThrow(
      () -> new RuntimeException("Pessoa não encontrada com o ID: " + id)
    );
    
    existingPeople.setFullName(people.getFullName() != null ? people.getFullName() : existingPeople.getFullName());
    existingPeople.setBirthDate(people.getBirthDate() != null ? people.getBirthDate() : existingPeople.getBirthDate());

    if (people.getMainAddress() == null) {
      people.setMainAddress(existingPeople.getMainAddress());
    }
    existingPeople.setMainAddress(people.getMainAddress());

    peopleRepository.save(existingPeople);
    peopleRepository.flush();

    return existingPeople;
  }
  
  public void deletePeopleById(Long id) {
    People people = peopleRepository.findById(id).orElseThrow(
      () -> new RuntimeException("Pessoa não encontrada com o ID: " + id)
    );
    List<Address> addresses = people.getAddresses();
    for (Address address : addresses) {
      addressRepository.deleteById(address.getId());
    }

    peopleRepository.deleteById(id);
  }
}
