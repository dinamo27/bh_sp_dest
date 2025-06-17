package com.inspire.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inspire.entity.InspireLockedCodesReplacedBy;

import java.util.List;

@Repository
public interface InspireLockedCodesReplacedByRepository extends JpaRepository<InspireLockedCodesReplacedBy, Long> {
    
    List<InspireLockedCodesReplacedBy> findByProjectIdAndReplacedByIsNullAndSupersededNotesIsNotNull(String projectId);
    
    @Modifying
    @Query("UPDATE InspireLockedCodesReplacedBy i SET i.replacedBy = :replacedBy, i.toBeVerified = 1 WHERE i.id = :id")
    int updateReplacedByAndToBeVerified(@Param("id") Long id, @Param("replacedBy") String replacedBy);
}