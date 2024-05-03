package com.pauloh.attus.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
public class People {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @NonNull
  private String fullName;
  
  @Nonnull
  private LocalDate birthDate;

  @OneToMany(mappedBy = "peopleId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  private List<Address> addresses;

  @OneToOne(fetch = FetchType.EAGER)
  private Address mainAddress;

  public void setFullName(String fullName) {
    if (!fullName.matches("^[A-Z][a-z]*(?:\\.[A-Z][a-z]*)*(?: [A-Z][a-z]*(?:\\.[A-Z][a-z]*)*)+$")
      ) {
      throw new IllegalArgumentException("Nome inválido: " + fullName);
    }

    this.fullName = fullName;
  }
}
