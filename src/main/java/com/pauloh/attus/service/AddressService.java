package com.pauloh.attus.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.pauloh.attus.model.Address;
import com.pauloh.attus.model.People;
import com.pauloh.attus.model.enums.AddressCategory;
import com.pauloh.attus.repository.AddressRepository;
import com.pauloh.attus.repository.PeopleRepository;

@Service
public class AddressService {
  private final AddressRepository addressRepository;
  private final PeopleRepository peopleRepository;
  
  public AddressService(
    AddressRepository addressRepository,
    PeopleRepository peopleRepository
    ) {
    this.addressRepository = addressRepository;
    this.peopleRepository = peopleRepository;
  }

  public void createAddress(Long id, Address address) {
    People people = peopleRepository.findById(id).orElseThrow(
      () -> new RuntimeException("Pessoa não encontrada com o ID: " + id)
    );
    AddressCategory addressCategory = address.getCategory();

    for (Address a : people.getAddresses()) {
      if (a.getCep().equals(address.getCep())
        && a.getNumber().equals(address.getNumber())) {
        throw new RuntimeException("Endereço ja existe");
      }
    }

    if (people.getMainAddress() == null) {
      address.setCategory(AddressCategory.MAIN);
      people.setMainAddress(address);
    }
    else if (addressCategory == null
      || addressCategory.equals(AddressCategory.ALTERNATIVE)) {
      address.setCategory(AddressCategory.ALTERNATIVE);
    }
    else if (addressCategory.equals(AddressCategory.MAIN)) {
      people.getMainAddress().setCategory(AddressCategory.ALTERNATIVE);
      
      address.setCategory(AddressCategory.MAIN);
      people.setMainAddress(address);
    }
    
    address.setPeopleId(people.getId());
    addressRepository.saveAndFlush(address);
    
    people.getAddresses().add(address);
    peopleRepository.save(people);
  }

  public Address findAddressById(Long id) {
    return addressRepository.findById(id).orElseThrow(
      () -> new RuntimeException("Endereço não encontrada com o ID: " + id));
  }
  
  public List<Address> findAllAddresses() {
    return addressRepository.findAll();
  }
  
  public void updateAddress(Long id, Address address) {
    Address existingAddress = addressRepository.findById(id).orElseThrow(
      () -> new RuntimeException("Endereço não encontrado com o ID: " + id)
    );

    People people = peopleRepository.findById(existingAddress.getPeopleId()).orElseThrow(
      () -> new RuntimeException("Pessoa não encontrada com o ID: " + id)
    );
    
    existingAddress.setStreetAddress(address.getStreetAddress() != null ? address.getStreetAddress() : existingAddress.getStreetAddress());
    existingAddress.setCep(address.getCep() != null ? address.getCep() : existingAddress.getCep());
    existingAddress.setNumber(address.getNumber() != null ? address.getNumber() : existingAddress.getNumber());
    existingAddress.setCity(address.getCity() != null ? address.getCity() : existingAddress.getCity());
    existingAddress.setState(address.getState() != null ? address.getState() : existingAddress.getState());

    if (address.getCategory() == null
      || address.getCategory().equals(AddressCategory.ALTERNATIVE)) {
      address.setCategory(existingAddress.getCategory());
    }
    else if (address.getCategory().equals(AddressCategory.MAIN)) {
      existingAddress.setCategory(AddressCategory.MAIN);
      people.setMainAddress(existingAddress);
      peopleRepository.save(people);
    }
    
    addressRepository.save(existingAddress);
  }
  
  public void deleteAddressById(Long id) {
    Address address = addressRepository.findById(id).orElseThrow(
      () -> new RuntimeException("Endereço não encontrado com o ID: " + id)
    );
    People people = peopleRepository.findById(address.getPeopleId()).orElseThrow(
      () -> new RuntimeException("Pessoa não encontrada com o ID: " + id)
    );

    if (address.getCategory().equals(AddressCategory.MAIN)
      && people.getAddresses().size() == 1) {
      people.setMainAddress(null);
    }
    else if (address.getCategory().equals(AddressCategory.MAIN)
      && people.getAddresses().size() == 2) {
      Address nextAddress = people.getAddresses().get(1);
      nextAddress.setCategory(AddressCategory.MAIN);
      people.setMainAddress(nextAddress);
    }
    
    people.getAddresses().remove(address);
    peopleRepository.save(people);
    
    addressRepository.deleteById(address.getId());
  }
}
