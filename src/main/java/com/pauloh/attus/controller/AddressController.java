package com.pauloh.attus.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.pauloh.attus.model.Address;
import com.pauloh.attus.service.AddressService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/address")
public class AddressController {

  private final AddressService addressService;
  
  public AddressController(AddressService addressService) {
    this.addressService = addressService;
  }
  
  @PostMapping("/peopleId={peopleId}")
  public ResponseEntity<Address> createAddress(@PathVariable Long peopleId, @RequestBody Address address) {
    addressService.createAddress(peopleId, address);
    return ResponseEntity.status(HttpStatus.CREATED).body(address);
  }
  
  @Async
  @GetMapping("/id={id}")
  public CompletableFuture<ResponseEntity<Address>> findAddressById(@PathVariable Long id) {
    return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.OK).body(addressService.findAddressById(id)));
  }
  
  @GetMapping("/all")
  public CompletableFuture<ResponseEntity<List<Address>>> findAllAddresses() {
    return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.OK).body(addressService.findAllAddresses()));
  }
  
  @PutMapping("/id={id}")
  public ResponseEntity<Address> updateAddress(@PathVariable Long id, @RequestBody Address address) {
    addressService.updateAddress(id, address);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body(address);
  }
  
  @DeleteMapping("/id={id}")
  public ResponseEntity<String> deleteAddressById(@PathVariable Long id) {
    addressService.deleteAddressById(id);
    return ResponseEntity.status(HttpStatus.ACCEPTED).body("Endereço de ID " + id + " deletado");
  }
}
