package com.example.demo.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Entity_staff;
@Repository
public interface DAO_staff extends JpaRepository<Entity_staff, Integer> {

    List<Entity_staff> findByNameContaining(String name);

    long countByRole(String role);
}

