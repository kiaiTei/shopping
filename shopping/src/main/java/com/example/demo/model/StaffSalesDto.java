package com.example.demo.model;

public class StaffSalesDto {

    private Integer staffId;
    private String staffName;
    private Integer totalSales;

    public StaffSalesDto(Integer staffId, String staffName, Integer totalSales) {
        this.setStaffId(staffId);
        this.setStaffName(staffName);
        this.setTotalSales(totalSales);
    }


	public Integer getStaffId() {
		return staffId;
	}

	public void setStaffId(Integer staffId) {
		this.staffId = staffId;
	}

	public String getStaffName() {
		return staffName;
	}

	public void setStaffName(String staffName) {
		this.staffName = staffName;
	}

	public Integer getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(Integer totalSales) {
		this.totalSales = totalSales;
	}

    // getter / setter
}
