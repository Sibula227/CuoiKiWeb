# QAUTE - Hệ thống Tư vấn Sinh viên Trực tuyến

**QAUTE** (Q&A UTE) là hệ thống hỗ trợ và tư vấn sinh viên trực tuyến, được xây dựng nhằm kết nối sinh viên với các phòng ban chức năng trong trường Đại học Sư phạm Kỹ thuật TP.HCM (HCMUTE). Hệ thống tích hợp Trí tuệ Nhân tạo (Gemini AI) để tự động hóa việc giải đáp các thắc mắc thường gặp, giúp sinh viên tiếp cận thông tin chính xác và kịp thời.


## 🚀 Tính năng chính

Hệ thống được thiết kế với 3 vai trò người dùng chính: **Sinh viên**, **Cố vấn (Advisor)**, và **Quản trị viên (Admin)**.

### 🌟 Dành cho Sinh viên
*   **Hỏi đáp trực tuyến:** Đặt câu hỏi cho các Phòng ban/Khoa chuyên trách.
*   **AI Chatbot:** Trợ lý ảo hỗ trợ giải đáp 24/7 về quy chế, thủ tục hành chính dựa trên Sổ tay Sinh viên.
*   **Tra cứu thông tin:** Tìm kiếm các câu hỏi đã được giải đáp, xem thông báo từ nhà trường.
*   **Profile cá nhân:** Quản lý thông tin tài khoản và lịch sử câu hỏi.
*   **Bản đồ:** Tra cứu vị trí các phòng ban trong trường.

### 👨‍🏫 Dành cho Cố vấn (Advisor)
*   **Quản lý câu hỏi:** Tiếp nhận và trả lời các câu hỏi gửi đến phòng ban mình phụ trách.
*   **Thống kê:** Xem báo cáo về số lượng câu hỏi và tình trạng xử lý.

### 🛡️ Dành cho Quản trị viên (Admin)
*   **Quản lý người dùng:** Thêm, sửa, xóa, phân quyền tài khoản.
*   **Quản lý danh mục:** Quản lý danh sách Khoa, Phòng ban.
*   **Dashboard:** Theo dõi toàn bộ hoạt động của hệ thống và xuất báo cáo.

## 🛠️ Công nghệ sử dụng

### Backend
*   **Java 17**
*   **Spring Boot 3.5.8**: Web, Data JPA, Security, Validation, Mail.
*   **Spring GraphQL**: Cho các truy vấn dữ liệu linh hoạt.
*   **Google Gemini AI (via `google-genai` SDK)**: Xử lý ngôn ngữ tự nhiên cho Chatbot.
*   **MySQL**: Hệ quản trị cơ sở dữ liệu chính.
*   **iTextPDF**: Xuất báo cáo PDF.

### Frontend
*   **Thymeleaf**: Template engine render giao diện phía server.
*   **HTML5 / CSS3 / JavaScript**: Giao diện người dùng hiện đại, responsive.
*   **Bootstrap / Custom CSS**: (Tuỳ thuộc vào implementation thực tế).

### Tools
*   **Maven**: Quản lý phụ thuộc và build dự án.
*   **Lombok**: Giảm thiểu code boilerplate.

## ⚙️ Cài đặt và Chạy ứng dụng

### Yêu cầu tiên quyết
*   JDK 17 trở lên.
*   Maven 3.6 trở lên.
*   MySQL Server.

### Các bước cài đặt

1.  **Clone dự án:**
    ```bash
    git clone https://github.com/Sibula227/CuoiKiWeb.git
    cd CuoiKiWeb
    ```

2.  **Cấu hình cơ sở dữ liệu:**
    Tạo database trong MySQL (ví dụ: `qaute_db`).
    Mở file `src/main/resources/application.properties` và cập nhật thông tin:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/qaute_db
    spring.datasource.username=your_username
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```

3.  **Cấu hình Gemini AI:**
    Thêm API Key của Google Gemini vào `application.properties`:
    ```properties
    gemini.api.key=YOUR_GEMINI_API_KEY
    ```

4.  **Cài đặt dependencies và Build:**
    ```bash
    ./mvnw clean install
    ```

5.  **Chạy ứng dụng:**
    ```bash
    ./mvnw spring-boot:run
    ```

6.  **Truy cập:**
    Mở trình duyệt và truy cập: `http://localhost:8080` (hoặc port được cấu hình).

## 📂 Cấu trúc dự án

```
CuoiKiWeb/
├── src/
│   ├── main/
│   │   ├── java/com/hcmute/qaute/  # Source code Java
│   │   ├── resources/
│   │       ├── prompts/            # Prompts cho AI
│   │       ├── templates/          # Thymeleaf templates
│   │       ├── static/             # CSS, JS, Images
│   │       └── application.properties
├── CHUONG_*.md                     # Tài liệu báo cáo đồ án
├── pom.xml                         # Maven configuration
└── README.md                       # File này
```

## 🤝 Đóng góp
Mọi đóng góp đều được hoan nghênh. Vui lòng tạo Pull Request hoặc mở Issue để thảo luận về các thay đổi.

## 📄 Giấy phép
Dự án được thực hiện bởi nhóm sinh viên HCMUTE cho môn học Lập trình Web.
