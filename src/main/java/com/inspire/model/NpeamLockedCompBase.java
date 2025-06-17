package com.inspire.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "npeam_locked_comp_base")
public class NpeamLockedCompBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private String projectId;

    @Lob
    @Column(name = "superseded_notes")
    private String supersededNotes;

    @Column(name = "suggested_material_code")
    private String suggestedMaterialCode;

    @Column(name = "to_be_verified")
    private Boolean toBeVerified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSupersededNotes() {
        return supersededNotes;
    }

    public void setSupersededNotes(String supersededNotes) {
        this.supersededNotes = supersededNotes;
    }

    public String getSuggestedMaterialCode() {
        return suggestedMaterialCode;
    }

    public void setSuggestedMaterialCode(String suggestedMaterialCode) {
        this.suggestedMaterialCode = suggestedMaterialCode;
    }

    public Boolean getToBeVerified() {
        return toBeVerified;
    }

    public void setToBeVerified(Boolean toBeVerified) {
        this.toBeVerified = toBeVerified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NpeamLockedCompBase that = (NpeamLockedCompBase) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "NpeamLockedCompBase{" +
                "id=" + id +
                ", projectId='" + projectId + '\'' +
                ", supersededNotes='" + supersededNotes + '\'' +
                ", suggestedMaterialCode='" + suggestedMaterialCode + '\'' +
                ", toBeVerified=" + toBeVerified +
                '}';
    }
}