package com.example.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface MaterialCodeSuggestionRepository extends JpaRepository<Object, Long> {

    @Query(value = "SELECT COUNT(1) > 0 FROM inspire_project WHERE project_id = :projectId", nativeQuery = true)
    boolean isProjectInInspire(@Param("projectId") String projectId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_parts SET mark = NULL WHERE project_id = :projectId", nativeQuery = true)
    int resetMarkFieldInInspireParts(@Param("projectId") String projectId);

    @Query(value = "SELECT id, superseded_notes FROM inspire_locked_codes_replaced_by " +
           "WHERE project_id = :projectId AND replaced_by IS NULL AND superseded_notes IS NOT NULL", 
           nativeQuery = true)
    List<Map<String, Object>> getInspireLockedCodesForProcessing(@Param("projectId") String projectId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE inspire_locked_codes_replaced_by SET replaced_by = :replacedBy, to_be_verified = 1 " +
           "WHERE id = :id", nativeQuery = true)
    int updateInspireLockedCodesReplacedBy(@Param("id") Long id, @Param("replacedBy") String replacedBy);

    @Query(value = "SELECT id, superseded_notes FROM npeam_locked_comp_base " +
           "WHERE project_id = :projectId AND suggested_material_code IS NULL AND superseded_notes IS NOT NULL", 
           nativeQuery = true)
    List<Map<String, Object>> getNpeamLockedCompBaseForProcessing(@Param("projectId") String projectId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE npeam_locked_comp_base SET suggested_material_code = :suggestedMaterialCode, to_be_verified = 1 " +
           "WHERE id = :id", nativeQuery = true)
    int updateNpeamLockedCompBaseSuggestedMaterialCode(@Param("id") Long id, @Param("suggestedMaterialCode") String suggestedMaterialCode);
}