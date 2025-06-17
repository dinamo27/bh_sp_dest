package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inspire.entity.NpeamLockedCompBase;

import java.util.List;

@Repository
public interface NpeamLockedCompBaseRepository extends JpaRepository<NpeamLockedCompBase, Long> {
    
    List<NpeamLockedCompBase> findByProjectIdAndSuggestedMaterialCodeIsNullAndSupersededNotesIsNotNull(String projectId);
    
    @Modifying
    @Query("UPDATE NpeamLockedCompBase n SET n.suggestedMaterialCode = :suggestedMaterialCode, n.toBeVerified = 1 WHERE n.id = :id")
    int updateSuggestedMaterialCodeAndToBeVerified(@Param("id") Long id, @Param("suggestedMaterialCode") String suggestedMaterialCode);
}