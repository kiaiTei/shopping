package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Entity_customer;

public interface CustomerRepository
        extends JpaRepository<Entity_customer, Integer> {

    @Query("""
        SELECT c FROM Entity_customer c
        WHERE
        (:customerNo IS NULL OR c.customerNo = :customerNo)
        AND (:customerName IS NULL OR c.customerName LIKE %:customerName%)
        AND (:address IS NULL OR c.address LIKE %:address%)
        AND (:phone IS NULL OR c.phone LIKE %:phone%)
    """)
    List<Entity_customer> search(
        @Param("customerNo") Integer customerNo,
        @Param("customerName") String customerName,
        @Param("address") String address,
        @Param("phone") String phone
    );
}
