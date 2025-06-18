package com.inspire.model;

import javax.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspire_project")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InspireProject {
    @Id
    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "status")
    private String status;
}