package com.inspire.repository;

import com.inspire.entity.NpeamLockedCompBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NpeamLockedCompBaseRepository extends JpaRepository<NpeamLockedCompBase, Long> {
    
    List<NpeamLockedCompBase> findByProjectIdAndSuggestedMaterialCodeIsNullAndSupersededNotesIsNotNull(String projectId);
    
    @Modifying
    @Query("UPDATE NpeamLockedCompBase SET suggestedMaterialCode = :suggestedMaterialCode, toBeVerified = :toBeVerified WHERE id = :id")
    void updateSuggestedMaterialCodeAndToBeVerified(
        @Param("id") Long id, 
        @Param("suggestedMaterialCode") String suggestedMaterialCode, 
        @Param("toBeVerified") Integer toBeVerified
    );
}