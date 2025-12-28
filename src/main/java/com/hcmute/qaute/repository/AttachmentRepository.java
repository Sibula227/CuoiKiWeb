package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    // Lấy tất cả file đính kèm của một câu hỏi
    List<Attachment> findByQuestionId(Long questionId);
    
    // Lấy file đính kèm của một câu trả lời
    List<Attachment> findByAnswerId(Long answerId);
}