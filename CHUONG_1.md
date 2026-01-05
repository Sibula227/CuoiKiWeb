# CHƯƠNG 1. KHẢO SÁT HIỆN TRẠNG & XÁC ĐỊNH YÊU CẦU

## 1.1. Khảo sát hiện trạng

Hiện nay, công tác tư vấn và hỗ trợ sinh viên tại các trường đại học thường gặp phải tình trạng quá tải do số lượng sinh viên lớn trong khi nhân sự phòng ban có hạn. Sinh viên thường gặp khó khăn trong việc tìm kiếm thông tin chính thống về quy chế đào tạo, thủ tục hành chính, hoặc thông tin liên lạc của các đơn vị chức năng. Các kênh thông tin hiện tại (website trường, fanpage, email) thường phân tán, thiếu tính tương tác trực tiếp hoặc phản hồi chậm.

Hệ thống **QAUTE** được xây dựng nhằm giải quyết bài toán này bằng cách tập trung hóa việc tiếp nhận câu hỏi và ứng dụng AI để tự động hóa khâu giải đáp thông tin thường gặp.

### 1.1.1. Đối tượng quản lý trong hệ thống

Dựa trên phân tích nghiệp vụ, hệ thống cần quản lý các đối tượng dữ liệu chính sau:

1.  **Người dùng (Users):**
    *   Lưu trữ thông tin định danh: Họ tên, Email, Tên đăng nhập, Mật khẩu (mã hóa), Số điện thoại.
    *   Thông tin sinh viên: Mã số sinh viên (MSSV), Lớp, Khoa.
    *   Trạng thái hoạt động và Vai trò (Role).

2.  **Phòng ban / Khoa (Departments):**
    *   Đại diện cho các đơn vị tư vấn (ví dụ: Phòng Đào tạo, Phòng Công tác Sinh viên, Khoa CNTT...).
    *   Thông tin: Tên phòng ban, Mô tả, Logo/Hình ảnh đại diện.

3.  **Câu hỏi tư vấn (Questions):**
    *   Là đối tượng chính để tương tác giữa Sinh viên và Cố vấn.
    *   Thông tin: Tiêu đề, Nội dung chi tiết, Thời gian tạo, Trạng thái (Chờ duyệt, Đang trả lời, Đã giải quyết).
    *   Gắn liền với một Sinh viên (người hỏi) và một Phòng ban (nơi nhận).

4.  **Câu trả lời / Phản hồi (Answers/Comments):**
    *   Nội dung giải đáp từ Cố vấn hoặc thảo luận từ Sinh viên khác.
    *   Thông tin: Nội dung, Người trả lời, Thời gian, Có phải câu trả lời chính thức hay không.

5.  **Phiên tư vấn tự động (Chat Sessions - AI):**
    *   Lưu trữ lịch sử hội thoại giữa Sinh viên và Chatbot AI.
    *   Thông tin: Nội dung tin nhắn (Prompt/Response), Thời gian, Ngữ cảnh.

6.  **Nhật ký hệ thống (System Logs):**
    *   Ghi lại các hành động quan trọng để phục vụ kiểm toán (Audit).
    *   Thông tin: Người thực hiện, Hành động (Thêm/Sửa/Xóa), Thời gian, Kết quả.

### 1.1.2. Phân quyền người dùng

Hệ thống được thiết kế theo mô hình phân quyền chặt chẽ (Role-Based Access Control - RBAC) với 3 nhóm người dùng chính:

1.  **Sinh viên (STUDENT):**
    *   Đăng ký, Đăng nhập hệ thống.
    *   Cập nhật thông tin cá nhân (Profile) và Ảnh đại diện.
    *   Đặt câu hỏi mới cho các Phòng ban/Khoa.
    *   Xem, tìm kiếm và tham gia thảo luận tại các câu hỏi công khai.
    *   Sử dụng Chatbot AI để tra cứu thông tin nhanh.
    *   Tra cứu bản đồ trường học.

2.  **Cố vấn học tập / Nhân viên phòng ban (ADVISOR):**
    *   Đăng nhập với tài khoản được cấp.
    *   Xem danh sách câu hỏi gửi đến Phòng ban của mình.
    *   Trả lời và duyệt câu hỏi của sinh viên.
    *   Xem thống kê cơ bản về hoạt động tư vấn.

3.  **Quản trị viên (ADMIN):**
    *   Có toàn quyền quản trị hệ thống.
    *   Quản lý danh sách Người dùng (Thêm, Khóa, Cấp lại mật khẩu).
    *   Quản lý danh mục Phòng ban/Khoa.
    *   Xem Nhật ký hoạt động (System Logs) để giám sát hệ thống.
    *   Xuất báo cáo thống kê tình hình tư vấn.

## 1.2. Xác định yêu cầu

### 1.2.1. Yêu cầu về chức năng nghiệp vụ

Hệ thống cần đáp ứng các nhóm chức năng cốt lõi sau:

**A. Nhóm chức năng Quản lý Tài khoản & Định danh:**
*   Đăng ký tài khoản mới (dành cho Sinh viên).
*   Đăng nhập/Đăng xuất bảo mật.
*   Quản lý Hồ sơ cá nhân: Cập nhật thông tin liên lạc, Thay đổi ảnh đại diện (Upload ảnh).

**B. Nhóm chức năng Tư vấn & Hỏi đáp (Forum):**
*   **Đặt câu hỏi:** Cho phép soạn thảo câu hỏi với định dạng văn bản phong phú (Rich Text), chọn chuyên mục (Khoa/Phòng) phù hợp.
*   **Hiển thị & Tìm kiếm:** Danh sách câu hỏi trực quan, hỗ trợ Lọc (theo Khoa), Sắp xếp (Mới nhất, Phổ biến, Chưa trả lời) và Tìm kiếm theo từ khóa.
*   **Phản hồi:** Cố vấn và Sinh viên có thể tham gia trả lời, bình luận dưới mỗi câu hỏi.

**C. Nhóm chức năng Trợ lý ảo (AI Chatbot):**
*   Tích hợp mô hình ngôn ngữ lớn (LLM - Gemini) để trả lời tự động.
*   Hỗ trợ ngữ cảnh (Context-aware): Trả lời dựa trên dữ liệu Sổ tay sinh viên và Quy chế đào tạo của trường.
*   Lưu trữ lịch sử đoạn chat để xem lại.

**D. Nhóm chức năng Quản trị (Admin Dashboard):**
*   Dashboard tổng quan: Biểu đồ thống kê số lượng câu hỏi, tỷ lệ giải quyết.
*   Quản lý danh mục: Thêm/Sửa/Xóa các Khoa và Phòng ban.
*   Quản lý người dùng: Phân quyền Advisors cho các phòng ban.
*   Báo cáo: Xuất dữ liệu thống kê ra file PDF/Excel.

**E. Nhóm chức năng Tiện ích:**
*   Tra cứu bản đồ: Bản đồ số của trường với khả năng thao tác (Zoom/Pan).

### 1.2.2. Yêu cầu phi chức năng

1.  **Hiệu năng (Performance):**
    *   Thời gian phản hồi cho các trang web thông thường < 2 giây.
    *   Thời gian phản hồi của Chatbot AI < 5-10 giây (tùy thuộc vào độ phức tạp của câu hỏi và API).
    *   Hệ thống chịu tải được nhiều sinh viên truy cập cùng lúc trong các đợt cao điểm.

2.  **Bảo mật (Security):**
    *   Mật khẩu người dùng phải được mã hóa (BCrypt) trong cơ sở dữ liệu.
    *   Phân quyền truy cập đúng vai trò (Authorization): Sinh viên không thể truy cập trang Admin.
    *   Bảo vệ chống các lỗi bảo mật web phổ biến (CSRF, XSS, SQL Injection).

3.  **Giao diện & Trải nghiệm người dùng (UI/UX):**
    *   Giao diện thiết kế theo phong cách hiện đại (Modern/Glassmorphism).
    *   Tương thích đa thiết bị (Responsive): Hiển thị tốt trên cả Máy tính và Điện thoại di động.
    *   Thao tác người dùng mượt mà, hạn chế tải lại trang toàn bộ (sử dụng AJAX/Fragments).

4.  **Tính sẵn sàng & Tin cậy (Availability & Reliability):**
    *   Hệ thống hoạt động ổn định 24/7.
    *   Cơ sở dữ liệu đảm bảo tính toàn vẹn (ACID), không mất mát dữ liệu quan trọng.
    *   Xử lý lỗi (Error Handling) thân thiện, có trang thông báo lỗi 404/500 rõ ràng.
