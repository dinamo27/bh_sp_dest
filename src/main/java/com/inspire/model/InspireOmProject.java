package com.inspire.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspire_om_project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspireOmProject {
    @Id
    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "status")
    private Character status;
}