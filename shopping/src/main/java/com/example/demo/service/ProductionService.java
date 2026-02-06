package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Production;
import com.example.demo.repository.ProductionRepository;

	 @Service
    public class ProductionService {

        @Autowired
        private ProductionRepository productionRepository;

        public List<Production> findAll() {
            return productionRepository.findAll();
        }
        
        public Production findById(int id) {
            return productionRepository.findById(id).orElse(null);
        }

        public void save(Production production) {
            productionRepository.save(production);
        }
        
        public List<Production> search(String keyword) {
            return productionRepository
                    .findByPNameContainingOrBrandContaining(keyword, keyword);
        }
        
        public void deleteById(Integer id) {
            productionRepository.deleteById(id);
        }
        public List<Production> searchByFields(Integer id, String pName, String brand, String priceRange) {
            int min = 0, max = Integer.MAX_VALUE;
            if (priceRange != null && !priceRange.isEmpty()) {
                String[] parts = priceRange.split("-");
                try {
                    min = Integer.parseInt(parts[0]);
                    if (parts.length > 1 && !parts[1].isEmpty()) {
                        max = Integer.parseInt(parts[1]);
                    }
                } catch (NumberFormatException e) {
                    // 不处理，使用默认
                }
            }

            return productionRepository.findByFields(id, pName, brand, min, max);
        }

    }