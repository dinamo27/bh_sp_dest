package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InspireTempPosActivityMappingId implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String projectId;
    private String activityId;
    private String positionId;
}