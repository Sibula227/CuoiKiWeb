package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.Question;
import com.hcmute.qaute.entity.enums.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 1. Cho Sinh viên: Xem lịch sử câu hỏi của chính mình
    List<Question> findByStudentId(Long studentId);

    // 2. Cho Admin/Tư vấn viên: Lọc câu hỏi theo phòng ban
    List<Question> findByDepartmentId(Integer departmentId);

    // 3. Cho Admin: Lọc theo trạng thái (Ví dụ: Tìm các câu PENDING để xử lý)
    List<Question> findByStatus(QuestionStatus status);

    // 4. Tìm kiếm (Search): Tìm trong tiêu đề HOẶC nội dung
    // Dùng Pageable để hỗ trợ phân trang luôn (cho xịn)
    @Query("SELECT q FROM Question q WHERE q.title LIKE %:keyword% OR q.content LIKE %:keyword%")
    Page<Question> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    // 5. Thống kê: Đếm số lượng câu hỏi theo trạng thái (Dùng vẽ biểu đồ Dashboard)
    long countByStatus(QuestionStatus status);
    
    List<Question> findByDepartmentId(Long departmentId);
    
}