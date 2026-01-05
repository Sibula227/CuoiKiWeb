## 4.4. Phân hệ Tiện ích mở rộng - Trợ lý ảo AI (Chatbot Widget)

### 4.4.1. Mô tả tính năng
Nhằm hỗ trợ sinh viên giải đáp thắc mắc tức thời mọi lúc mọi nơi mà không làm gián đoạn trải nghiệm duyệt web, hệ thống tích hợp một tiện ích Chatbot dưới dạng Widget trôi nổi. Biểu tượng Chatbot được đặt tinh tế ở góc phải màn hình, luôn hiển thị dù người dùng đang ở bất kỳ trang nào. Khi kích hoạt, một cửa sổ đối thoại hiện đại sẽ xuất hiện, cho phép sinh viên đặt câu hỏi bằng ngôn ngữ tự nhiên và nhận câu trả lời ngay lập tức. Đây đóng vai trò như một "Tư vấn viên ảo" trực tuyến 24/7.

### 4.4.2. Phân tích giải pháp kỹ thuật
Để đảm bảo tính linh hoạt và khả năng mở rộng trong tương lai, module Chatbot được thiết kế với tư duy module hóa (Modular Design) cao độ:

Đầu tiên, về mặt giao diện, Chatbot được xây dựng như một thành phần độc lập (Independent Component). Nhờ sử dụng cơ chế Fragment của Thymeleaf, mã nguồn của Chatbot tách biệt hoàn toàn với bố cục chính của trang web. Điều này mang lại lợi ích lớn trong việc bảo trì: các lập trình viên có thể nâng cấp, thay đổi giao diện hoặc thậm chí tạm ẩn tính năng này mà không gây ảnh hưởng hay xung đột với các thành phần khác của hệ thống.

Thứ hai, về mặt xử lý thông minh, hệ thống đã vượt qua giới hạn của các chatbot kịch bản thông thường (Rule-based) bằng việc tích hợp sâu với Trí tuệ nhân tạo.
*   **Tích hợp Generative AI:** Thay vì chỉ trả lời theo các từ khóa cứng nhắc được lập trình sẵn, hệ thống kết nối với Google Gemini API thông qua các Endpoints bảo mật phía Backend.
*   **Xử lý ngữ cảnh (Context Awareness):** Dữ liệu đầu vào từ sinh viên được tiền xử lý và gửi kèm với ngữ cảnh hệ thống (System Prompt) liên quan đến quy chế đào tạo của trường. Nhờ đó, câu trả lời do AI sinh ra không chỉ tự nhiên, mạch lạc mà còn đảm bảo độ chính xác cao về mặt nghiệp vụ sư phạm.

Cách tiếp cận này biến Chatbot từ một công cụ tra cứu tĩnh trở thành một trợ lý thông minh thực thụ, có khả năng học hỏi và ngày càng hoàn thiện trong quá trình hỗ trợ sinh viên.
