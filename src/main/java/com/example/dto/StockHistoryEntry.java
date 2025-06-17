package com.example.dto;

import java.util.Date;
import java.util.Objects;

public class StockHistoryEntry {
    private Long id;
    private Long productId;
    private Date changeDate;
    private String changeType;
    private Double quantityChange;
    private Long locationId;
    private String locationName;
    private Long createdBy;
    private String createdByUsername;
    private String notes;
    
    public StockHistoryEntry() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Date getChangeDate() {
        return changeDate;
    }
    
    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }
    
    public String getChangeType() {
        return changeType;
    }
    
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
    
    public Double getQuantityChange() {
        return quantityChange;
    }
    
    public void setQuantityChange(Double quantityChange) {
        this.quantityChange = quantityChange;
    }
    
    public Long getLocationId() {
        return locationId;
    }
    
    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }
    
    public String getLocationName() {
        return locationName;
    }
    
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    
    public Long getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatedByUsername() {
        return createdByUsername;
    }
    
    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockHistoryEntry that = (StockHistoryEntry) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(productId, that.productId) &&
               Objects.equals(changeDate, that.changeDate) &&
               Objects.equals(changeType, that.changeType) &&
               Objects.equals(quantityChange, that.quantityChange) &&
               Objects.equals(locationId, that.locationId) &&
               Objects.equals(locationName, that.locationName) &&
               Objects.equals(createdBy, that.createdBy) &&
               Objects.equals(createdByUsername, that.createdByUsername) &&
               Objects.equals(notes, that.notes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, productId, changeDate, changeType, quantityChange,
                           locationId, locationName, createdBy, createdByUsername, notes);
    }
    
    @Override
    public String toString() {
        return "StockHistoryEntry{" +
               "id=" + id +
               ", productId=" + productId +
               ", changeDate=" + changeDate +
               ", changeType='" + changeType + '\'' +
               ", quantityChange=" + quantityChange +
               ", locationId=" + locationId +
               ", locationName='" + locationName + '\'' +
               ", createdBy=" + createdBy +
               ", createdByUsername='" + createdByUsername + '\'' +
               ", notes='" + notes + '\'' +
               '}';
    }
}