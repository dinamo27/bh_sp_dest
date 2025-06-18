package com.inspire.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspire_parts_grouped_recalc")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspirePartsGroupedRecalc {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Integer projectId;

    @ManyToOne
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private InspireProject inspireProject;

    @Column(name = "processed_flag")
    private Character processedFlag;
}