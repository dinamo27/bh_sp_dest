package com.inspire.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspire_spir_refresh_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"projectId"})
public class InspireSpirRefreshData {
    
    @Id
    @Column(name = "project_id")
    private Integer projectId;
    
    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private InspireProject inspireProject;
    
    @Column(name = "processed_flag")
    private Character processedFlag;
}