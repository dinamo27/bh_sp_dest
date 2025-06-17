package com.inspire.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "inspire_locked_codes_replaced_by")
public class InspireLockedCodesReplacedBy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id")
    private String projectId;

    @Column(name = "locked_code")
    private String lockedCode;

    @Column(name = "replaced_by", nullable = true)
    private String replacedBy;

    @Lob
    @Column(name = "superseded_notes")
    private String supersededNotes;

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

    public String getLockedCode() {
        return lockedCode;
    }

    public void setLockedCode(String lockedCode) {
        this.lockedCode = lockedCode;
    }

    public String getReplacedBy() {
        return replacedBy;
    }

    public void setReplacedBy(String replacedBy) {
        this.replacedBy = replacedBy;
    }

    public String getSupersededNotes() {
        return supersededNotes;
    }

    public void setSupersededNotes(String supersededNotes) {
        this.supersededNotes = supersededNotes;
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
        InspireLockedCodesReplacedBy that = (InspireLockedCodesReplacedBy) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "InspireLockedCodesReplacedBy{" +
                "id=" + id +
                ", projectId='" + projectId + '\'' +
                ", lockedCode='" + lockedCode + '\'' +
                ", replacedBy='" + replacedBy + '\'' +
                ", supersededNotes='" + supersededNotes + '\'' +
                ", toBeVerified=" + toBeVerified +
                '}';
    }
}