package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Production;

public interface ProductionRepository extends JpaRepository<Production, Integer> {
	List<Production> findByPNameContainingOrBrandContaining(
            String pName, String brand  );
}
