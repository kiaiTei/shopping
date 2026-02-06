package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.Production;

public interface ProductionRepository extends JpaRepository<Production, Integer> {
	List<Production> findByPNameContainingOrBrandContaining(
            String pName, String brand  );
	
	@Query("SELECT p FROM Production p WHERE "
		     + "(:id IS NULL OR p.pId = :id) AND "
		     + "(:pName IS NULL OR p.pName LIKE %:pName%) AND "
		     + "(:brand IS NULL OR p.brand LIKE %:brand%) AND "
		     + "(p.price BETWEEN :min AND :max)")
		List<Production> findByFields(@Param("id") Integer id,
		                              @Param("pName") String pName,
		                              @Param("brand") String brand,
		                              @Param("min") int min,
		                              @Param("max") int max);

}