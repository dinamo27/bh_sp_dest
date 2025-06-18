package com.example.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@Table(name = "inspire_parts_grouped_recalc", schema = "inspire")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PartsGroupedRecalc.PartsGroupedRecalcId.class)
public class PartsGroupedRecalc {
    
    @Id
    @Column(name = "project_id")
    private Integer projectId;
    
    @Column(name = "processed")
    private Boolean processed;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartsGroupedRecalcId implements Serializable {
        private Integer projectId;
    }
}