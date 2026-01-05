# CHƯƠNG 2. CƠ SỞ LÝ THUYẾT VÀ CÔNG NGHỆ ÁP DỤNG

Trong chương này, nhóm nghiên cứu sẽ trình bày chi tiết về kiến trúc tổng thể của hệ thống cũng như các công nghệ nền tảng được lựa chọn để xây dựng giải pháp. Việc lựa chọn công nghệ không chỉ dựa trên sự phổ biến mà còn căn cứ vào các tiêu chí về độ ổn định, khả năng mở rộng, tính bảo mật và hiệu năng xử lý, đáp ứng các yêu cầu nghiệp vụ phức tạp của một hệ thống tư vấn trực tuyến quy mô lớn.

## 2.1. Kiến trúc hệ thống

Hệ thống QAUTE được thiết kế dựa trên mô hình kiến trúc ba lớp (Three-Tier Architecture), một mô hình chuẩn mực trong phát triển phần mềm doanh nghiệp, giúp tách biệt rõ ràng giữa các thành phần giao diện, xử lý nghiệp vụ và lưu trữ dữ liệu. Kiến trúc này bao gồm:

**Lớp Trình diễn (Presentation Layer):** Đảm nhận vai trò tương tác với người dùng cuối, hiển thị dữ liệu và tiếp nhận các yêu cầu đầu vào. Lớp này được xây dựng linh hoạt để đảm bảo trải nghiệm đồng nhất trên đa nền tảng thiết bị.

**Lớp Nghiệp vụ (Business Logic Layer):** Chứa toàn bộ các quy tắc xử lý nghiệp vụ cốt lõi của hệ thống như xác thực người dùng, xử lý luồng tư vấn, và tích hợp trí tuệ nhân tạo.

**Lớp Truy cập Dữ liệu (Data Access Layer):** Chịu trách nhiệm giao tiếp trực tiếp với cơ sở dữ liệu, đảm bảo tính toàn vẹn và nhất quán của thông tin được lưu trữ.

Đồng thời, hệ thống tuân thủ nghiêm ngặt mô hình thiết kế MVC (Model-View-Controller) trong việc tổ chức mã nguồn, giúp tăng cường khả năng bảo trì và phát triển tính năng mới trong tương lai.

## 2.2. Nền tảng công nghệ phía Máy chủ (Backend)

Nhóm nghiên cứu quyết định lựa chọn ngôn ngữ lập trình **Java** (phiên bản JDK 17 LTS) làm nền tảng phát triển chính. Đây là ngôn ngữ lập trình hướng đối tượng mạnh mẽ, có tính độc lập nền tảng cao và sở hữu một hệ sinh thái hỗ trợ khổng lồ. Đặc biệt, phiên bản Java 17 với chế độ hỗ trợ dài hạn đảm bảo sự ổn định và tối ưu hóa hiệu năng cho các ứng dụng vận hành liên tục.

Để xây dựng phần lõi của ứng dụng, nhóm sử dụng **Spring Boot 3.x**, một framework hiện đại được xây dựng trên nền tảng Spring Framework. Spring Boot cung cấp cơ chế "Convention over Configuration" (quy ước hơn cấu hình), giúp giảm thiểu đáng kể thời gian thiết lập môi trường và tập trung tối đa vào việc phát triển logic nghiệp vụ.

Trong hệ sinh thái Spring, **Spring Security** đóng vai trò then chốt trong việc bảo vệ hệ thống. Framework này cung cấp các cơ chế xác thực và phân quyền mạnh mẽ, bảo vệ ứng dụng trước các nguy cơ tấn công phổ biến như CSRF (Cross-Site Request Forgery) hay Session Fixation. Việc tích hợp Spring Security đảm bảo rằng dữ liệu nhạy cảm của sinh viên và nhà trường luôn được bảo vệ an toàn.

Đối với việc tương tác cơ sở dữ liệu, hệ thống sử dụng **Spring Data JPA** kết hợp với **Hibernate ORM**. Công nghệ này cho phép các nhà phát triển thao tác với dữ liệu thông qua các đối tượng Java (POJO) thay vì phải viết các câu lệnh SQL thuần túy phức tạp. Điều này không chỉ giúp tăng tốc độ phát triển mà còn giảm thiểu các lỗi liên quan đến cú pháp truy vấn và tăng cường khả năng bảo trì mã nguồn.

Điểm nhấn công nghệ của hệ thống là việc tích hợp **Google Gemini API** – mô hình ngôn ngữ lớn (LLM) tiên tiến. Việc ứng dụng Generative AI vào module chat giúp hệ thống có khả năng hiểu và xử lý ngôn ngữ tự nhiên, từ đó đưa ra các phản hồi tư vấn tự động, chính xác và tùy biến theo ngữ cảnh người dùng, giải quyết bài toán nhân sự tư vấn một cách hiệu quả.

## 2.3. Công nghệ Giao diện người dùng (Frontend)

Tại lớp giao diện, hệ nhận thấy sự cần thiết của việc kết hợp giữa khả năng xử lý phía máy chủ và trải nghiệm tương tác phía máy khách. Do đó, **Thymeleaf** được lựa chọn làm Template Engine chính. Với cơ chế Render phía Server (Server-Side Rendering), Thymeleaf cho phép hiển thị nội dung động một cách SEO-friendly và tích hợp sâu với Spring MVC, đảm bảo dữ liệu được kiểm soát chặt chẽ trước khi gửi tới trình duyệt.

Về mặt thiết kế trực quan, hệ thống áp dụng ngôn ngữ thiết kế **Glassmorphism** (hiệu ứng kính mờ) kết hợp với **Bootstrap 5**, một framework CSS hàng đầu hiện nay. Bootstrap cung cấp hệ thống lưới (Grid System) linh hoạt, giúp giao diện tự động thích ứng với mọi kích thước màn hình (Responsive Design), từ máy tính cá nhân đến các thiết bị di động. Các tương tác người dùng như tìm kiếm thời gian thực, lọc dữ liệu hay cửa sổ chat được hỗ trợ bởi **JavaScript** thuần (Vanilla JS) và các thư viện hỗ trợ như FontAwesome, mang lại trải nghiệm mượt mà và trực quan.

## 2.4. Hệ quản trị Cơ sở dữ liệu

Dữ liệu là tài sản quan trọng nhất của mọi hệ thống thông tin. Nhóm nghiên cứu lựa chọn **MySQL**, hệ quản trị cơ sở dữ liệu quan hệ (RDBMS) mã nguồn mở phổ biến nhất thế giới. MySQL nổi tiếng với tốc độ truy xuất nhanh, độ tin cậy cao và khả năng mở rộng tốt.

Cơ sở dữ liệu được thiết kế ở dạng chuẩn hóa (Normalization) để giảm thiểu dư thừa dữ liệu và đảm bảo tính nhất quán. Việc sử dụng các ràng buộc toàn vẹn khóa ngoại (Foreign Keys) và Transaction (Giao dịch) đảm bảo dữ liệu luôn chính xác ngay cả khi xảy ra sự cố trong quá trình xử lý.

Tổng kết lại, việc kết hợp giữa sức mạnh tính toán, bảo mật của Java Spring Boot ở phía Backend, sự linh hoạt, hiện đại của Bootstrap/Thymeleaf ở phía Frontend và độ tin cậy của MySQL tạo nên một nền tảng công nghệ vững chắc, đáp ứng đầy đủ các yêu cầu khắt khe về kỹ thuật và nghiệp vụ của đề tài.
