package com.pauloh.attus.model;

import com.pauloh.attus.model.enums.AddressCategory;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Address{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @NonNull
  private String streetAddress;
  
  @NonNull
  private Integer cep;

  @NonNull
  private Integer number;

  @NonNull
  private String city;

  @NonNull
  private String state;

  private AddressCategory category;
  
  private Long peopleId;

  public Address(String streetAddress, int cep, int number, String city, String state, AddressCategory category) {
    this.streetAddress = streetAddress;
    this.cep = cep;
    this.number = number;
    this.city = city;
    this.state = state;
    this.category = category;
  }
}