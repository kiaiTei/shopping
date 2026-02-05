package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Entity_customer;
import com.example.demo.service.CustomerService;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // 一覧表示（主入口）
    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customer/list";
    }
    
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) Integer customerNo,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String phone,
            Model model) {

        List<Entity_customer> result =
                customerService.search(
                        customerNo,
                        emptyToNull(customerName),
                        emptyToNull(address),
                        emptyToNull(phone)
                );

        model.addAttribute("customers", result);
        return "customer/list";
    }

    private String emptyToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    // 新規登録画面
    @GetMapping("/new")
    public String newCustomer(Model model) {
        model.addAttribute("customer", new Entity_customer());
        return "customer/form";
    }

    // 保存
    @PostMapping("/save")
    public String save(@ModelAttribute Entity_customer customer) {
        customerService.save(customer);
        return "redirect:/customer";
    }

    // 編集
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        // 直接调用 findById，已经返回实体对象
        Entity_customer customer = customerService.findById(id);
        model.addAttribute("customer", customer);
        return "customer/form";
    }



    // 削除
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        customerService.deleteById(id);
        return "redirect:/customer";
    }
}
