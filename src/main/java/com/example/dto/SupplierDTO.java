package com.example.dto;

public class SupplierDTO {
    
    private Long id;
    private String companyName;
    private String contactName;
    private String contactTitle;
    
    public SupplierDTO() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getContactName() {
        return contactName;
    }
    
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
    
    public String getContactTitle() {
        return contactTitle;
    }
    
    public void setContactTitle(String contactTitle) {
        this.contactTitle = contactTitle;
    }
}