package com.inspire.repository;

import com.inspire.entity.InspireLockedCodesReplacedBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InspireLockedCodesReplacedByRepository extends JpaRepository<InspireLockedCodesReplacedBy, Long> {
    
    List<InspireLockedCodesReplacedBy> findByProjectIdAndReplacedByIsNullAndSupersededNotesIsNotNull(String projectId);
    
    @Modifying
    @Query("UPDATE InspireLockedCodesReplacedBy SET replacedBy = :replacedBy, toBeVerified = :toBeVerified WHERE id = :id")
    void updateReplacedByAndToBeVerified(@Param("id") Long id, @Param("replacedBy") String replacedBy, @Param("toBeVerified") Integer toBeVerified);
}