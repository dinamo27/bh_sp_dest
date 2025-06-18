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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "processed_flag")
    private Character processedFlag;
}