# CHƯƠNG 5. CÁC ĐIỂM NHẤN KỸ THUẬT VÀ TỐI ƯU HỆ THỐNG

Trong quá trình xây dựng hệ thống QAUTE, bên cạnh việc đáp ứng các yêu cầu nghiệp vụ cơ bản, nhóm phát triển đã tập trung giải quyết các bài toán kỹ thuật chuyên sâu nhằm đảm bảo tính bảo mật, hiệu năng và khả năng bảo trì của mã nguồn. Chương này sẽ trình bày chi tiết về các giải pháp kỹ thuật nổi bật đã được áp dụng.

## 5.1. Tối ưu hóa Dữ liệu và Kiến trúc phân tầng (DTO Pattern)

Một trong những vấn đề cốt lõi khi thiết kế hệ thống lớn là việc kiểm soát luồng dữ liệu giữa các lớp. Hệ thống không trả trực tiếp các thực thể (Entity) của cơ sở dữ liệu ra giao diện người dùng, mà áp dụng triệt để mẫu thiết kế **Data Transfer Object (DTO)**.

Việc tách biệt hoàn toàn giữa lớp lưu trữ và lớp trình diễn mang lại nhiều lợi ích quan trọng:
*   **Bảo mật thông tin:** DTO cho phép lọc bỏ các trường dữ liệu nhạy cảm (như mật khẩu, token, thông tin định danh nội bộ) trước khi gửi phản hồi về phía trình duyệt, ngăn chặn nguy cơ rò rỉ dữ liệu.
*   **Tùy biến hiển thị:** Các lớp DTO như `QuestionResponseDTO` hay `AnswerDTO` được tích hợp sẵn các logic chuyển đổi dữ liệu hiển thị. Ví dụ, thay vì hiển thị thời gian thô (Timestamp), hệ thống tự động tính toán và trả về định dạng thân thiện với người dùng như "2 giờ trước" hay "vừa xong", nâng cao trải nghiệm người dùng ngay từ tầng dữ liệu.

## 5.2. Tổ chức Giao diện theo Hướng Module (Thymeleaf Fragments)

Về mặt Frontend, tính nhất quán và khả năng tái sử dụng mã nguồn được đặt lên hàng đầu thông qua kỹ thuật **Thymeleaf Layouts & Fragments**. Thay vì lặp lại mã HTML một cách thủ công ở từng trang, hệ thống xây dựng một khung xương (Layout) chung chứa các thành phần cố định như Thanh điều hướng (Navbar), Thanh bên (Sidebar) và Chân trang (Footer).

Cách tiếp cận này giúp việc bảo trì trở nên cực kỳ hiệu quả: mọi thay đổi về cấu trúc hay giao diện chung chỉ cần thực hiện một lần tại file gốc (`layout.html`) và sẽ tự động cập nhật trên toàn bộ hàng chục trang con của hệ thống. Điều này đảm bảo tính đồng bộ tuyệt đối về giao diện và giảm thiểu rủi ro lỗi hiển thị.

## 5.3. Bảo mật Đa lớp với Spring Security

Cơ chế bảo mật của QAUTE không chỉ dừng lại ở trang đăng nhập mà được tích hợp sâu vào từng phương thức xử lý (Controller) của hệ thống.

*   **Xác thực và Phân quyền (Authentication & Authorization):** Hệ thống kiểm soát chặt chẽ quyền truy cập dựa trên vai trò (Role). Sinh viên chỉ có quyền truy cập vùng dữ liệu cá nhân, trong khi các tính năng quản trị được cô lập hoàn toàn cho Admin.
*   **Bảo mật cấp độ Đối tượng (Object-Level Security):** Logic xử lý nghiệp vụ được thiết kế để kiểm tra quyền sở hữu dữ liệu ngay tại Backend. Ví dụ, một sinh viên chỉ được phép chỉnh sửa hoặc xóa câu hỏi/bình luận do chính mình tạo ra. Mọi nỗ lực can thiệp vào dữ liệu của người khác thông qua việc giả mạo API đều bị chặn đứng và từ chối xử lý, đảm bảo tính toàn vẹn dữ liệu.

## 5.4. Cơ chế Xử lý Lỗi tập trung (Global Exception Handling)

Để nâng cao trải nghiệm người dùng ngay cả khi hệ thống gặp sự cố, nhóm đã triển khai cơ chế **Xử lý ngoại lệ toàn cục (Global Exception Handler)** sử dụng `@ControllerAdvice`.

Thay vì để lộ các thông báo lỗi kỹ thuật (Stack Trace) khó hiểu và thiếu chuyên nghiệp khi người dùng truy cập sai đường dẫn hoặc gặp lỗi server, hệ thống sẽ tự động điều hướng họ đến các trang thông báo lỗi được thiết kế riêng (như trang 404 Not Found hay 500 Internal Server Error). Các trang này không chỉ thông báo tình trạng lỗi một cách nhẹ nhàng, dễ hiểu mà còn cung cấp các nút điều hướng để người dùng quay lại trang chủ, tránh cảm giác hoang mang và cụt đường (dead-end).
