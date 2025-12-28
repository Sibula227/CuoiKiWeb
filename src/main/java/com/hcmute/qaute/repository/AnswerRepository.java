package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    // Lấy list câu trả lời của 1 câu hỏi cụ thể, sắp xếp cũ nhất lên trước
    List<Answer> findByQuestionIdOrderByCreatedAtAsc(Long questionId);
}