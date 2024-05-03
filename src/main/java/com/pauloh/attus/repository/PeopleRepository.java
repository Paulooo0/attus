package com.pauloh.attus.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pauloh.attus.model.People;

@Repository
public interface PeopleRepository extends JpaRepository<People, Long> {
  Optional<People> findByFullNameAndBirthDate(String fullName, LocalDate birthDate);
}
