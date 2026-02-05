package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Entity_staff;
import com.example.demo.repository.DAO_staff;

@Service
public class UserService {

    @Autowired
    private DAO_staff userRepository;

    // 登录
    public Entity_staff login(int id, String password) {
        return userRepository.findById(id)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }

    public List<Entity_staff> findAll() {
        return userRepository.findAll();
    }

    public List<Entity_staff> search(Integer id, String name) {
        if (id != null) {
            return userRepository.findById(id)
                    .map(List::of)
                    .orElse(List.of());
        }
        if (name != null && !name.isBlank()) {
            return userRepository.findByNameContaining(name);
        }
        return userRepository.findAll();
    }

    public Entity_staff findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    public void save(Entity_staff staff) {
        userRepository.save(staff);
    }

    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }
}
