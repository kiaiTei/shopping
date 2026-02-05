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
        
        public Production findById(Integer id) {
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
    }
