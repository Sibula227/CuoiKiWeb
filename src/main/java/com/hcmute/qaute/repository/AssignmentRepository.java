package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.Assignment;
import com.hcmute.qaute.entity.enums.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    // Tìm nhiệm vụ được giao cho nhân viên X
    List<Assignment> findByAssignedToId(Long userId);

    // Tìm xem câu hỏi này đang được phân công cho ai (Active assignment)
    Optional<Assignment> findByQuestionIdAndStatus(Long questionId, AssignmentStatus status);
}