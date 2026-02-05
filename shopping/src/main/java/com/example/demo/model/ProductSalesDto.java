package com.example.demo.model;

public class ProductSalesDto {
    private Integer productId;
    private String productName;
    private Integer totalSales;

    public ProductSalesDto(Integer productId, String productName, Integer totalSales) {
        this.setProductId(productId);
        this.setProductName(productName);
        this.setTotalSales(totalSales);
    }

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(Integer totalSales) {
		this.totalSales = totalSales;
	}

    // getter / setter
}

