# CHƯƠNG 3. PHÂN TÍCH VÀ THIẾT KẾ CƠ SỞ DỮ LIỆU

Hệ thống QAUTE được xây dựng dựa trên mô hình dữ liệu quan hệ (Relational Database) với các thực thể được thiết kế chặt chẽ nhằm đảm bảo tính toàn vẹn dữ liệu và hỗ trợ tốt cho các nghiệp vụ tư vấn phức tạp. Tổng thể cơ sở dữ liệu bao gồm 3 nhóm thực thể chính: Quản lý Định danh, Nghiệp vụ Hỏi - Đáp và Tiện ích Hỗ trợ.

## 3.1. Nhóm thực thể Quản lý Người dùng & Phân quyền

Đây là nhóm cốt lõi chịu trách nhiệm lưu trữ thông tin định danh và kiểm soát quyền truy cập của toàn bộ hệ thống.

1.  **User (Người dùng):**
    *   **Mô tả:** Lưu trữ thông tin chi tiết của mọi đối tượng sử dụng hệ thống (Sinh viên, Cố vấn, Admin).
    *   **Thuộc tính quan trọng:** `username`, `password_hash` (mật khẩu mã hóa), `email`, `student_id` (MSSV - quan trọng đối với sinh viên), `avatar_url` (Link ảnh đại diện), và `full_name`.
    *   **Mối quan hệ:** Liên kết nhiều-một (Many-to-One) với Role và Department.

2.  **Role (Vai trò):**
    *   **Mô tả:** Định nghĩa các nhóm quyền hạn trong hệ thống để thực hiện phân quyền (Authorization).
    *   **Các giá trị chính:**
        *   `STUDENT`: Quyền hạn cơ bản, xem và đặt câu hỏi.
        *   `ADVISOR`: Quyền tiếp nhận và trả lời câu hỏi theo chuyên môn.
        *   `ADMIN`: Quyền quản trị toàn bộ hệ thống và xem báo cáo.

3.  **Department (Phòng ban/Đơn vị):**
    *   **Mô tả:** Danh mục các đơn vị tiếp nhận câu hỏi tư vấn (ví dụ: Phòng Đào tạo, Phòng CTSV, Khoa CNTT).
    *   **Vai trò:** Là thực thể đích mà câu hỏi hướng tới. Mỗi Cố vấn (Advisor) sẽ thuộc về một Department cụ thể.

4.  **AuditLog (Nhật ký kiểm toán):**
    *   **Mô tả:** Ghi lại toàn bộ lịch sử tác động dữ liệu nhạy cảm.
    *   **Thuộc tính:** `action` (Thêm/Sửa/Xóa), `entity_name` (Bảng bị tác động), `user_id` (Người thực hiện), `details` (Chi tiết thay đổi).
    *   **Mục đích:** Hỗ trợ Admin tra cứu trách nhiệm và giám sát an ninh hệ thống.

## 3.2. Nhóm thực thể Nghiệp vụ Hỏi - Đáp

Nhóm này lưu trữ nội dung cốt lõi của diễn đàn, nơi diễn ra sự tương tác giữa Sinh viên và Nhà trường.

5.  **Question (Câu hỏi):**
    *   **Mô tả:** Thực thể trung tâm của hệ thống.
    *   **Thuộc tính:** `title` (Tiêu đề), `content` (Nội dung câu hỏi - hỗ trợ Rich Text), `priority` (Độ ưu tiên), `view_count` (Lượt xem).
    *   **Trạng thái (Status):** Quản lý vòng đời câu hỏi (`PENDING` - Chờ duyệt, `ANSWERED` - Đã trả lời).
    *   **Mối quan hệ:** Liên kết với User (Người hỏi) và Department (Nơi nhận).

6.  **Answer (Câu trả lời):**
    *   **Mô tả:** Nội dung phản hồi cho các câu hỏi.
    *   **Vai trò:** Hệ thống phân biệt rõ câu trả lời chính thức từ Cố vấn (Official Answer) và các bình luận thảo luận thêm.

## 3.3. Nhóm thực thể Tiện ích & Tương tác thông minh (AI Integration)

Để đáp ứng yêu cầu về công nghệ hiện đại, hệ thống bổ sung các thực thể phục vụ cho tính năng Trí tuệ nhân tạo và Tương tác thời gian thực.

7.  **ChatSession (Phiên Chat AI):**
    *   **Mô tả:** Quản lý một phiên hội thoại giữa Sinh viên và Chatbot.
    *   **Mục đích:** Giúp hệ thống ghi nhớ ngữ cảnh (Context) của cuộc trò chuyện, cho phép Chatbot trả lời liên tục và mạch lạc (Multi-turn conversation).

8.  **ChatMessage (Tin nhắn Chat):**
    *   **Mô tả:** Lưu trữ chi tiết từng cặp câu hỏi - câu trả lời trong một phiên chat.
    *   **Thuộc tính:** `role` (user/model) để phân biệt tin nhắn của người dùng hay của AI, `content` (Nội dung tin nhắn - hỗ trợ Markdown).

9.  **Notification (Thông báo - Dự kiến):**
    *   **Mô tả:** Lưu trữ các sự kiện cần thông báo cho người dùng (ví dụ: "Câu hỏi của bạn đã được trả lời").
    *   **Cơ chế:** Hỗ trợ tính năng thông báo thời gian thực (Real-time) trong các phiên bản nâng cấp tiếp theo.

---
*Tổng kết:* Với thiết kế CSDL này, hệ thống đảm bảo khả năng lưu trữ dữ liệu có cấu trúc, hỗ trợ truy vấn nhanh cho các tính năng tìm kiếm/lọc, đồng thời sẵn sàng cho việc mở rộng các tính năng AI và báo cáo thống kê phức tạp.
