package com.example.dto;

import java.util.Date;
import java.util.Objects;

public class StockHistorySummary {
    private Double totalPurchased;
    private Double totalSold;
    private Double totalReturned;
    private Double totalLost;
    private Double totalAdjusted;
    private Double netChange;
    private Long totalTransactions;
    private Date periodStart;
    private Date periodEnd;
    private Double currentStock;
    
    public StockHistorySummary() {
        this.totalPurchased = 0.0;
        this.totalSold = 0.0;
        this.totalReturned = 0.0;
        this.totalLost = 0.0;
        this.totalAdjusted = 0.0;
        this.netChange = 0.0;
        this.totalTransactions = 0L;
        this.currentStock = 0.0;
    }
    
    public Double getTotalPurchased() {
        return totalPurchased;
    }
    
    public void setTotalPurchased(Double totalPurchased) {
        this.totalPurchased = totalPurchased;
    }
    
    public Double getTotalSold() {
        return totalSold;
    }
    
    public void setTotalSold(Double totalSold) {
        this.totalSold = totalSold;
    }
    
    public Double getTotalReturned() {
        return totalReturned;
    }
    
    public void setTotalReturned(Double totalReturned) {
        this.totalReturned = totalReturned;
    }
    
    public Double getTotalLost() {
        return totalLost;
    }
    
    public void setTotalLost(Double totalLost) {
        this.totalLost = totalLost;
    }
    
    public Double getTotalAdjusted() {
        return totalAdjusted;
    }
    
    public void setTotalAdjusted(Double totalAdjusted) {
        this.totalAdjusted = totalAdjusted;
    }
    
    public Double getNetChange() {
        return netChange;
    }
    
    public void setNetChange(Double netChange) {
        this.netChange = netChange;
    }
    
    public Long getTotalTransactions() {
        return totalTransactions;
    }
    
    public void setTotalTransactions(Long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }
    
    public Date getPeriodStart() {
        return periodStart;
    }
    
    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }
    
    public Date getPeriodEnd() {
        return periodEnd;
    }
    
    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }
    
    public Double getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(Double currentStock) {
        this.currentStock = currentStock;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockHistorySummary that = (StockHistorySummary) o;
        return Objects.equals(totalPurchased, that.totalPurchased) &&
               Objects.equals(totalSold, that.totalSold) &&
               Objects.equals(totalReturned, that.totalReturned) &&
               Objects.equals(totalLost, that.totalLost) &&
               Objects.equals(totalAdjusted, that.totalAdjusted) &&
               Objects.equals(netChange, that.netChange) &&
               Objects.equals(totalTransactions, that.totalTransactions) &&
               Objects.equals(periodStart, that.periodStart) &&
               Objects.equals(periodEnd, that.periodEnd) &&
               Objects.equals(currentStock, that.currentStock);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(totalPurchased, totalSold, totalReturned, totalLost,
                           totalAdjusted, netChange, totalTransactions,
                           periodStart, periodEnd, currentStock);
    }
    
    @Override
    public String toString() {
        return "StockHistorySummary{" +
               "totalPurchased=" + totalPurchased +
               ", totalSold=" + totalSold +
               ", totalReturned=" + totalReturned +
               ", totalLost=" + totalLost +
               ", totalAdjusted=" + totalAdjusted +
               ", netChange=" + netChange +
               ", totalTransactions=" + totalTransactions +
               ", periodStart=" + periodStart +
               ", periodEnd=" + periodEnd +
               ", currentStock=" + currentStock +
               '}';
    }
}