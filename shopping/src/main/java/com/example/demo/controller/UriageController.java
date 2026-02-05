package com.example.demo.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Entity_customer;
import com.example.demo.model.Entity_purchase;
import com.example.demo.model.Entity_staff;
import com.example.demo.model.ProductSalesDto;
import com.example.demo.model.Production;
import com.example.demo.model.StaffSalesDto;
import com.example.demo.service.CustomerService;
import com.example.demo.service.ProductionService;
import com.example.demo.service.PurchaseService;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/uriage")
public class UriageController {

    @Autowired
    private PurchaseService purchaseService;
    @Autowired
    private ProductionService productionService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;


    // ===== 売上一覧 =====
    @GetMapping("/list")
    public String list(Model model,
             @RequestParam(required = false) Integer id,
             @RequestParam(required = false) String productName,
             @RequestParam(required = false) String staffName,
             @RequestParam(required = false) String customerName,
             @RequestParam(required = false) String priceRange) {

        // 取得所有购买记录
        List<Entity_purchase> list = purchaseService.findAll();

        List<Map<String, Object>> viewList = new ArrayList<>();

        for (Entity_purchase p : list) {
            Production product = productionService.findById(p.getProductId());
            Entity_staff staff = userService.findById(p.getStaffId());
            Entity_customer customer = customerService.findById(p.getCustomerId());

            int totalPrice = p.getTotalPrice();

            // ===== 过滤条件 =====
            if (id != null && !p.getId().equals(id)) continue;
            if (productName != null && !productName.isEmpty() && !product.getpName().contains(productName)) continue;
            if (staffName != null && !staffName.isEmpty() && !staff.getName().contains(staffName)) continue;
            if (customerName != null && !customerName.isEmpty() && !customer.getCustomerName().contains(customerName)) continue;

            if (priceRange != null && !priceRange.isEmpty()) {
                switch (priceRange) {
                    case "0-1000":
                        if (totalPrice > 1000) continue;
                        break;
                    case "1000-5000":
                        if (totalPrice < 1000 || totalPrice > 5000) continue;
                        break;
                    case "5000-10000":
                        if (totalPrice < 5000 || totalPrice > 10000) continue;
                        break;
                    case "10000-":
                        if (totalPrice < 10000) continue;
                        break;
                }
            }

            Map<String, Object> row = new HashMap<>();
            row.put("id", p.getId());
            row.put("productName", product.getpName());
            row.put("staffName", staff.getName());
            row.put("customerName", customer.getCustomerName());
            row.put("quantity", p.getQuantity());
            row.put("totalPrice", totalPrice);
            row.put("createdAtFormatted", p.getCreatedAt() != null ?
                    p.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "");

            viewList.add(row);
        }

        // 把筛选条件也加入 model，页面才能保持输入值
        model.addAttribute("list", viewList);
        model.addAttribute("id", id);
        model.addAttribute("productName", productName);
        model.addAttribute("staffName", staffName);
        model.addAttribute("customerName", customerName);
        model.addAttribute("priceRange", priceRange);

        return "uriage/list";
    }

 // ===== 修改输入页面 =====
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Entity_purchase purchase = purchaseService.findById(id);
        if (purchase == null) {
            return "redirect:/uriage/list";
        }

        Production product = productionService.findById(purchase.getProductId());
        Entity_staff staff = userService.findById(purchase.getStaffId());
        Entity_customer customer = customerService.findById(purchase.getCustomerId());

        model.addAttribute("purchase", purchase);
        model.addAttribute("product", product);
        model.addAttribute("staff", staff);
        model.addAttribute("customer", customer);

        // 下拉选择所有商品
        model.addAttribute("products", productionService.findAll());

        return "uriage/edit"; // 对应 templates/uriage/edit.html
    }


    // ===== 修改确认页面 =====
    @PostMapping("/edit/confirm")
    public String editConfirm(
            @RequestParam Integer purchaseId,
            @RequestParam Integer productId,
            @RequestParam Integer quantity,
            Model model) {

        Entity_purchase purchase = purchaseService.findById(purchaseId);
        Production product = productionService.findById(productId);

        int totalPrice = product.getPrice() * quantity;

        model.addAttribute("purchaseId", purchaseId);
        model.addAttribute("product", product);
        model.addAttribute("quantity", quantity);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("customerId", purchase.getCustomerId());
        model.addAttribute("staffId", purchase.getStaffId());

        return "uriage/edit_confirm";
    }

    // ===== 修改保存 =====
    @PostMapping("/edit/save")
    public String editSave(
            @RequestParam Integer purchaseId,
            @RequestParam Integer productId,
            @RequestParam Integer quantity,
            @RequestParam Integer totalPrice) {

        Entity_purchase purchase = purchaseService.findById(purchaseId);

        purchase.setProductId(productId);
        purchase.setQuantity(quantity);
        purchase.setTotalPrice(totalPrice);
        purchase.setCreatedAt(LocalDateTime.now());

        purchaseService.save(purchase);

        return "redirect:/uriage/list";
    }
    
    
    @GetMapping("/production")
    public String productSummary(Model model) {

        List<Entity_purchase> purchases = purchaseService.findAll();

        // productId -> totalPrice 累加
        Map<Integer, Integer> salesMap = new HashMap<>();

        for (Entity_purchase p : purchases) {
            salesMap.merge(
                    p.getProductId(),
                    p.getTotalPrice(),
                    Integer::sum
            );
        }

        List<ProductSalesDto> summaryList = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : salesMap.entrySet()) {
            Integer productId = entry.getKey();
            Integer totalSales = entry.getValue();

            Production product = productionService.findById(productId);

            summaryList.add(
                    new ProductSalesDto(
                            productId,
                            product.getpName(),
                            totalSales
                    )
            );
        }

        model.addAttribute("summaryList", summaryList);
        return "uriage/production";
    }
    
    @GetMapping("/staff")
    public String staffSummary(Model model) {

        List<Entity_purchase> purchases = purchaseService.findAll();

        // staffId → 売上合計
        Map<Integer, Integer> salesMap = new HashMap<>();

        for (Entity_purchase p : purchases) {
            salesMap.merge(
                    p.getStaffId(),
                    p.getTotalPrice(),
                    Integer::sum
            );
        }

        List<StaffSalesDto> summaryList = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : salesMap.entrySet()) {
            Integer staffId = entry.getKey();
            Integer totalSales = entry.getValue();

            Entity_staff staff = userService.findById(staffId);

            summaryList.add(
                    new StaffSalesDto(
                            staffId,
                            staff.getName(),
                            totalSales
                    )
            );
        }

        model.addAttribute("summaryList", summaryList);
        return "uriage/staff";
    }
    
    
    @GetMapping("/export")
    public void exportExcel(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String staffName,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String priceRange,
            HttpServletResponse response) throws Exception {

        List<Entity_purchase> list = purchaseService.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("売上一覧");

        Row header = sheet.createRow(0);
        String[] titles = {
                "ID", "商品名", "担当者", "顧客", "数量", "合計金額", "購入日時"
        };
        for (int i = 0; i < titles.length; i++) {
            header.createCell(i).setCellValue(titles[i]);
        }

        int rowNum = 1;

        for (Entity_purchase p : list) {

            Production product = productionService.findById(p.getProductId());
            Entity_staff staff = userService.findById(p.getStaffId());
            Entity_customer customer = customerService.findById(p.getCustomerId());

            // ===== 筛选条件 =====
            if (id != null && !p.getId().equals(id)) continue;
            if (productName != null && !productName.isEmpty()
                    && !product.getpName().contains(productName)) continue;
            if (staffName != null && !staffName.isEmpty()
                    && !staff.getName().contains(staffName)) continue;
            if (customerName != null && !customerName.isEmpty()
                    && !customer.getCustomerName().contains(customerName)) continue;

            int totalPrice = p.getTotalPrice();
            if (priceRange != null && !priceRange.isEmpty()) {
                switch (priceRange) {
                    case "0-1000":
                        if (totalPrice > 1000) continue;
                        break;
                    case "1000-2000":
                        if (totalPrice < 1000 || totalPrice > 5000) continue;
                        break;
                    case "2000-10000":
                        if (totalPrice < 5000 || totalPrice > 10000) continue;
                        break;
                    case "10000-":
                        if (totalPrice < 10000) continue;
                        break;
                }
            }

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(p.getId());
            row.createCell(1).setCellValue(product.getpName());
            row.createCell(2).setCellValue(staff.getName());
            row.createCell(3).setCellValue(customer.getCustomerName());
            row.createCell(4).setCellValue(p.getQuantity());
            row.createCell(5).setCellValue(totalPrice);
            row.createCell(6).setCellValue(
                    p.getCreatedAt() == null ? "" :
                            p.getCreatedAt().toString()
            );
        }

        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=uriage_list.xlsx"
        );

        workbook.write(response.getOutputStream());
        workbook.close();
    }
    
//    @GetMapping("/uriage/production/export")
//    public void exportProductSales(HttpServletResponse response) throws IOException {
//
//        // 文件设置
//        response.setContentType(
//            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//        response.setHeader(
//            "Content-Disposition",
//            "attachment; filename=product_sales.xlsx");
//
//        Workbook workbook = new XSSFWorkbook();
//        Sheet sheet = workbook.createSheet("商品別売上");
//
//        // ===== header =====
//        Row header = sheet.createRow(0);
//        header.createCell(0).setCellValue("商品ID");
//        header.createCell(1).setCellValue("商品名");
//        header.createCell(2).setCellValue("総売上金額");
//
//        // 这里换成你实际取得 summaryList 的方法
//        List<ProductSalesDto> summaryList =uriageService.getProductSalesSummary();
//
//
//        int rowNum = 1;
//        for (ProductSalesDto dto : summaryList) {
//            Row row = sheet.createRow(rowNum++);
//            row.createCell(0).setCellValue(dto.getProductId());
//            row.createCell(1).setCellValue(dto.getProductName());
//            row.createCell(2).setCellValue(dto.getTotalSales());
//        }
//
//        // 自动列宽
//        for (int i = 0; i < 3; i++) {
//            sheet.autoSizeColumn(i);
//        }
//
//        workbook.write(response.getOutputStream());
//        workbook.close();
//    }
//
//
//

}
