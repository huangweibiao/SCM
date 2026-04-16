package com.scm.repository;

import com.scm.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 客户Repository
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByCustomerNameContaining(String keyword);

    List<Customer> findByStatus(Integer status);
}
