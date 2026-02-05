package com.example.demo.controller;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Entity_purchase;
import com.example.demo.model.Entity_staff;
import com.example.demo.model.Production;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ProductionService;
import com.example.demo.service.PurchaseService;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    private ProductionService productionService;

    @Autowired
    private PurchaseService purchaseService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CustomerService customerService;

    // ===== 购入输入画面 =====
    @GetMapping("/new")
    public String newPurchase(
            @RequestParam Integer pId,
            HttpSession session,
            Model model) {

    	Entity_staff staff = (Entity_staff) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/login_employee";
        }


        Production product = productionService.findById(pId);

        model.addAttribute("product", product);
        model.addAttribute("staff", staff);

        return "purchase/form";
    }

    // ===== 确认画面 =====
    @PostMapping("/confirm")
    public String confirm(
            @RequestParam Integer productId,
            @RequestParam Integer customerId,
            @RequestParam Integer quantity,
            HttpSession session,
            Model model) {

    	Entity_staff staff = (Entity_staff) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/login_employee";
        }


        Production product = productionService.findById(productId);
        int totalPrice = product.getPrice() * quantity;

        model.addAttribute("product", product);
        model.addAttribute("staff", staff);
        model.addAttribute("customerId", customerId);
        model.addAttribute("quantity", quantity);
        model.addAttribute("totalPrice", totalPrice);

        return "purchase/confirm";
    }

    // ===== 保存订单 =====
    @PostMapping("/save")
    public String save(
            @RequestParam Integer productId,
            @RequestParam Integer staffId,
            @RequestParam Integer customerId,
            @RequestParam Integer quantity,
            @RequestParam Integer totalPrice) {

        Entity_purchase p = new Entity_purchase();
        p.setProductId(productId);
        p.setStaffId(staffId);
        p.setCustomerId(customerId);
        p.setQuantity(quantity);
        p.setTotalPrice(totalPrice);

        // ★ 新增：提交时间
        p.setCreatedAt(LocalDateTime.now());

        purchaseService.save(p);

        return "redirect:/production";
    }
    
   


}
