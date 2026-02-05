package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Entity_purchase;
import com.example.demo.repository.PurchaseRepository;
@Service
public class PurchaseService {
	@Autowired
    private PurchaseRepository purchaseRepository;

    // 保存订单
    public Entity_purchase save(Entity_purchase purchase) {
        return purchaseRepository.save(purchase);
    }

    // 查询所有订单
    public List<Entity_purchase> findAll() {
        return purchaseRepository.findAll();
    }

    // 根据ID查询
    public Entity_purchase findById(Integer id) {
        return purchaseRepository.findById(id).orElse(null);
    }

}
