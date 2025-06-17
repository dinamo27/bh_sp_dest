package com.example.dto;

import java.util.List;
import java.util.Objects;

public class StockHistoryResponse {
    private List<StockHistoryEntry> history;
    private PaginationMetadata pagination;
    private StockHistorySummary summary;
    
    public StockHistoryResponse() {
    }
    
    public List<StockHistoryEntry> getHistory() {
        return history;
    }
    
    public void setHistory(List<StockHistoryEntry> history) {
        this.history = history;
    }
    
    public PaginationMetadata getPagination() {
        return pagination;
    }
    
    public void setPagination(PaginationMetadata pagination) {
        this.pagination = pagination;
    }
    
    public StockHistorySummary getSummary() {
        return summary;
    }
    
    public void setSummary(StockHistorySummary summary) {
        this.summary = summary;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StockHistoryResponse that = (StockHistoryResponse) o;
        return Objects.equals(history, that.history) &&
               Objects.equals(pagination, that.pagination) &&
               Objects.equals(summary, that.summary);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(history, pagination, summary);
    }
    
    @Override
    public String toString() {
        return "StockHistoryResponse{" +
               "history=" + history +
               ", pagination=" + pagination +
               ", summary=" + summary +
               '}';
    }
}