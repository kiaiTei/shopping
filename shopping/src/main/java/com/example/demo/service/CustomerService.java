package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Entity_customer;
import com.example.demo.repository.CustomerRepository;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // 查找所有客户
    public List<Entity_customer> findAll() {
        return customerRepository.findAll();
    }

    // 根据ID查找客户，找不到抛异常
    public Entity_customer findById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found, id=" + id));
    }

    // 保存或更新客户
    public Entity_customer save(Entity_customer customer) {
        return customerRepository.save(customer);
    }

    // 删除客户
    public void deleteById(Integer id) {
        customerRepository.deleteById(id);
    }

    // 模糊查询客户
    public List<Entity_customer> search(
            Integer customerNo,
            String customerName,
            String address,
            String phone) {
        return customerRepository.search(customerNo, customerName, address, phone);
    }
}
