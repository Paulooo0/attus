package com.pauloh.attus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pauloh.attus.model.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

}
