# CHƯƠNG 6. KẾT LUẬN VÀ HƯỚNG PHÁT TRIỂN

## 6.1. Kết quả đạt được và Đánh giá hệ thống

Sau quá trình nghiên cứu, thiết kế và triển khai, nhóm đã hoàn thành việc xây dựng **Hệ thống Tư vấn Sinh viên Trực tuyến (QAUTE)** đáp ứng được các mục tiêu đề ra ban đầu. Hệ thống không chỉ giải quyết được bài toán kết nối giữa sinh viên và nhà trường mà còn mang lại trải nghiệm người dùng hiện đại và hiệu quả.

### Những tính năng đã hoàn thiện và kiểm thử thành công:
Nhóm đã tiến hành kiểm thử chức năng (Functional Testing) kỹ lưỡng trên các phân hệ chính và đạt kết quả ổn định:
*   **Phân hệ Người dùng:** Quy trình Đăng ký, Đăng nhập, và Quản lý hồ sơ cá nhân hoạt động trơn tru. Cơ chế xác thực và bảo mật thông tin người dùng được đảm bảo.
*   **Phân hệ Hỏi đáp (Forum):** Sinh viên có thể đặt câu hỏi, tìm kiếm thông tin theo từ khóa và lọc theo Khoa/Phòng. Quy trình gửi - duyệt - trả lời câu hỏi diễn ra chính xác theo luồng nghiệp vụ.
*   **Phân hệ Trợ lý ảo AI:** Đã tích hợp thành công Google Gemini API. Chatbot có khả năng hiểu câu hỏi tự nhiên và phản hồi chính xác dựa trên dữ liệu quy chế đào tạo được huấn luyện.
*   **Phân hệ Tiện ích:** Tính năng Bản đồ số hoạt động tốt với khả năng tương tác (Zoom/Pan), hỗ trợ đắc lực cho sinh viên tra cứu vị trí.
*   **Phân hệ Quản trị:** Dashboard cung cấp cái nhìn tổng quan về hoạt động hệ thống, quản lý người dùng và danh mục hiệu quả.

### Ưu điểm của hệ thống:
1.  **Tính thực tiễn cao:** Sát với nhu cầu thực tế của môi trường đại học, giải quyết được điểm nghẽn trong khâu tư vấn và giải đáp thắc mắc.
2.  **Công nghệ hiện đại:** Áp dụng các công nghệ tiên tiến (Spring Boot, AI Integration, Glassmorphism UI) giúp hệ thống có hiệu năng tốt, bảo mật cao và giao diện bắt mắt.
3.  **Kiến trúc mở:** Thiết kế module hóa cho phép dễ dàng nâng cấp, bảo trì và tích hợp thêm các tính năng mới trong tương lai.

## 6.2. Hạn chế và Hướng phát triển

Bên cạnh những kết quả đạt được, hệ thống vẫn còn một số điểm hạn chế cần được cải thiện cũng như nhiều tiềm năng để phát triển mở rộng trong các giai đoạn tiếp theo.

### Hạn chế hiện tại:
*   **Nội dung huấn luyện AI:** Dữ liệu huấn luyện cho Chatbot mới chỉ dừng lại ở các quy chế và thông tin cơ bản, chưa bao phủ hết mọi tình huống xử lý học vụ phức tạp.
*   **Tính năng Thời gian thực (Real-time):** Hiện tại, hệ thống phản hồi dựa trên cơ chế Request-Response truyền thống, người dùng cần tải lại trang hoặc chờ đợi để thấy các cập nhật mới nhất (như câu trả lời mới).

### Hướng phát triển trong tương lai (Future Work):
Để hệ thống QAUTE trở nên hoàn thiện và thông minh hơn, nhóm đề xuất các hướng phát triển sau:

1.  **Nâng cấp khả năng của AI:**
    *   Mở rộng tập dữ liệu huấn luyện (Knowledge Base) bao gồm các tình huống tư vấn chuyên sâu hơn.
    *   Tích hợp khả năng "Học tăng cường" (Reinforcement Learning) để Chatbot tự cải thiện câu trả lời dựa trên đánh giá của sinh viên.

2.  **Tích hợp Công nghệ WebSocket:**
    *   Xây dựng cơ chế thông báo thời gian thực (Real-time Notification): Sinh viên sẽ nhận được thông báo ngay lập tức trên thanh tiêu đề khi có giảng viên trả lời câu hỏi mà không cần F5 trang.
    *   Phát triển tính năng Chat trực tuyến (Live Chat) giữa Sinh viên và Tư vấn viên khi Chatbot không giải quyết được vấn đề.

3.  **Phát triển Ứng dụng Di động (Mobile App):**
    *   Xây dựng phiên bản ứng dụng trên nền tảng Android/iOS sử dụng chung API Backend hiện có, giúp sinh viên tiếp cận hệ thống thuận tiện hơn trên thiết bị di động.

4.  **Phân tích Dữ liệu nâng cao (Advanced Analytics):**
    *   Tích hợp các công cụ phân tích dữ liệu để vẽ biểu đồ xu hướng quan tâm của sinh viên theo thời gian thực, giúp nhà trường chủ động hơn trong công tác quản lý và điều chỉnh chính sách.
