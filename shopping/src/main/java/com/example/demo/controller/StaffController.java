package com.example.demo.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.model.Entity_staff;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private UserService userService;

    /* =====================
       一览
       ===================== */
    @GetMapping
    public String list(HttpSession session, Model model) {
        Entity_staff loginUser = (Entity_staff) session.getAttribute("staff");
        if (loginUser == null) {
            return "redirect:/login_employee";
        }

        model.addAttribute("staffList", userService.findAll());
        model.addAttribute("loginUser", loginUser);
        return "staff/list";
    }

    /* =====================
       编辑
       ===================== */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id,
                       HttpSession session,
                       Model model) {

        Entity_staff loginUser = (Entity_staff) session.getAttribute("staff");
        if (loginUser == null) {
            return "redirect:/login_employee";
        }

        // 普通用户只能编辑自己
        if (!"ADMIN".equals(loginUser.getRole()) && loginUser.getId() != id) {
            return "redirect:/staff?error=forbidden";
        }

        Entity_staff staff = userService.findById(id);
        if (staff == null) {
            return "redirect:/staff";
        }

        // ⚠️ 关键：不把密码带到前端
        staff.setPassword("");

        model.addAttribute("staff", staff);
        model.addAttribute("loginUser", loginUser);
        return "staff/form";
    }

    /* =====================
       保存 / 更新
       ===================== */
    @PostMapping("/save")
    public String save(@ModelAttribute Entity_staff staff,
                       HttpSession session) {

        Entity_staff loginUser = (Entity_staff) session.getAttribute("staff");
        if (loginUser == null) {
            return "redirect:/login_employee";
        }

        // 普通用户只能更新自己
        if (!"ADMIN".equals(loginUser.getRole())
                && loginUser.getId() != staff.getId()) {
            return "redirect:/staff?error=forbidden";
        }

        Entity_staff original = userService.findById(staff.getId());

        // ✅ 没输入密码 → 保留原密码
        if (staff.getPassword() == null || staff.getPassword().isBlank()) {
            staff.setPassword(original.getPassword());
        }

        // 普通用户不能改角色
        if (!"ADMIN".equals(loginUser.getRole())) {
            staff.setRole(original.getRole());
        }

        userService.save(staff);
        return "redirect:/staff";
    }

    /* =====================
       删除（管理员）
       ===================== */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id,
                         HttpSession session) {

        Entity_staff loginUser = (Entity_staff) session.getAttribute("staff");
        if (loginUser == null || !"ADMIN".equals(loginUser.getRole())) {
            return "redirect:/staff?error=forbidden";
        }

        userService.deleteById(id);
        return "redirect:/staff";
    }
}
