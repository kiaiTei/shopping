package com.example.demo.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Entity_staff;
import com.example.demo.model.Production;
import com.example.demo.service.ProductionService;
import com.example.demo.service.UserService;

@Controller
public class FrontController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductionService productionService;


    @GetMapping("/login_employee")
    public String loginPage() {
        return "login_employee";
    }

    @PostMapping("/login_employee")
    public String login_employee(
            @RequestParam int id,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        Entity_staff staff = userService.login(id, password);

        if (staff != null) {
            session.setAttribute("staff", staff); // ⭐ 存整个 staff 对象
            return "redirect:/staff_page";
        } else {
            model.addAttribute("error", "ID或密码错误");
            return "login_employee";
        }
    }

    @GetMapping("/staff_page")
    public String staffPage(HttpSession session, Model model) {
        Entity_staff staff = (Entity_staff) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/login_employee";
        }
        model.addAttribute("staff", staff);
        return "staff_page";
    }



    
    @GetMapping("/production")
    public String productionList(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String pName,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String priceRange,
            HttpSession session,
            Model model
    ) {
        Entity_staff staff = (Entity_staff) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/login_employee";
        }

        List<Production> list;

        if ((id == null) && (pName == null || pName.isEmpty()) &&
            (brand == null || brand.isEmpty()) && (priceRange == null || priceRange.isEmpty())) {
            // 关键字全空 → 显示全部
            list = productionService.findAll();
        } else {
            // 根据条件筛选
            list = productionService.searchByFields(id, pName, brand, priceRange);
        }

        model.addAttribute("all_production", list);
        model.addAttribute("staff", staff);

        // 保留前端选中值
        model.addAttribute("id", id);
        model.addAttribute("pName", pName);
        model.addAttribute("brand", brand);
        model.addAttribute("priceRange", priceRange);

        return "production";
    }



    
    @GetMapping("/new_pro")
    public String addProductionPage(HttpSession session) {
    	Entity_staff staff = (Entity_staff) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/login_employee";
        }

        return "new_pro";
    }
    
    @PostMapping("/new_pro")
    public String addProduction(
            @RequestParam String p_name,
            @RequestParam String brand,
            @RequestParam String size,
            @RequestParam Integer price,
            HttpSession session
    ) {
    	Entity_staff staff = (Entity_staff) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/login_employee";
        }


        Production p = new Production();
        p.setpName(p_name);
        p.setBrand(brand);
        p.setSize(size);
        p.setPrice(price);

        productionService.save(p);

        return "redirect:/production";
    }
    
    @GetMapping("/production/search")
    public String searchProduction(
            @RequestParam(required = false, defaultValue = "") String keyword,
            HttpSession session,
            Model model
    ) {
        Entity_staff staff = (Entity_staff) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/login_employee";
        }

        List<Production> result;
        if (keyword.isEmpty()) {
            result = productionService.findAll(); // 没关键词就显示全部
        } else {
            result = productionService.search(keyword); // 有关键词就搜索
        }

        model.addAttribute("keyword", keyword);
        model.addAttribute("all_production", result);
        model.addAttribute("staff", staff);

        return "production";
    }
    
    @GetMapping("/production/edit")
    public String editProduction(
            @RequestParam Integer id,
            HttpSession session,
            Model model
    ) {
    	Entity_staff staff = (Entity_staff) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/login_employee";
        }


        Production production = productionService.findById(id);
        if (production == null) {
            return "redirect:/production";
        }

        model.addAttribute("production", production);
        return "production_edit";
    }
    
    @PostMapping("/production/edit")
    public String updateProduction(
            @RequestParam Integer pId,
            @RequestParam String pName,
            @RequestParam String brand,
            @RequestParam String size,
            @RequestParam Integer price,
            HttpSession session
    ) {
    	Entity_staff staff = (Entity_staff) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/login_employee";
        }


        Production p = new Production();
        p.setPId(pId); // ⭐ 必须有 ID
        p.setpName(pName);
        p.setBrand(brand);
        p.setSize(size);
        p.setPrice(price);

        productionService.save(p); // JPA 会自动做 UPDATE

        return "redirect:/production";
    }
    
    @GetMapping("/production/delete")
    public String deleteProduction(
            @RequestParam Integer id,
            HttpSession session
    ) {
        // 登录检查
    	Entity_staff staff = (Entity_staff) session.getAttribute("staff");
        if (staff == null) {
            return "redirect:/login_employee";
        }


        productionService.deleteById(id);

        return "redirect:/production";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 清空登录信息
        session.invalidate();
        // 返回登录页
        return "redirect:/login_employee";
    }

    
}