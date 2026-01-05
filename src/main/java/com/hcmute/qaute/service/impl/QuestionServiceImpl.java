package com.hcmute.qaute.service.impl;

import com.hcmute.qaute.dto.QuestionCreateDTO;
import com.hcmute.qaute.dto.QuestionResponseDTO;
import com.hcmute.qaute.entity.Department;
import com.hcmute.qaute.entity.Question;
import com.hcmute.qaute.entity.User;
import com.hcmute.qaute.entity.enums.QuestionStatus;
import com.hcmute.qaute.repository.DepartmentRepository;
import com.hcmute.qaute.repository.QuestionRepository;
import com.hcmute.qaute.repository.UserRepository;
import com.hcmute.qaute.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private com.hcmute.qaute.service.TagService tagService;

    @Autowired
    private com.hcmute.qaute.repository.AttachmentRepository attachmentRepository;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public QuestionResponseDTO createQuestion(QuestionCreateDTO dto, String username, String tagInput,
            org.springframework.web.multipart.MultipartFile[] files) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Question question = new Question();
        question.setTitle(dto.getTitle());
        question.setContent(dto.getContent());
        question.setStudent(student);
        question.setStatus(QuestionStatus.PENDING);

        question.setStudentFaculty(dto.getFaculty());
        question.setStudentCohort(dto.getCohort());

        if (dto.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Phòng ban không tồn tại!"));
            question.setDepartment(dept);
        }

        // --- 1. Xử lý Tags ---
        if (tagInput != null && !tagInput.trim().isEmpty()) {
            java.util.Set<com.hcmute.qaute.entity.Tag> tags = tagService.processTags(tagInput);
            question.setTags(tags);
            question.setTagsCached(
                    tags.stream().map(com.hcmute.qaute.entity.Tag::getSlug).collect(Collectors.joining(", ")));
        }
        // ---------------------

        question.setCreatedAt(LocalDateTime.now());
        Question saved = questionRepository.save(question);

        // --- 2. Xử lý Attachments ---
        if (files != null && files.length > 0) {
            for (org.springframework.web.multipart.MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
                        // Lưu file vào thư mục upload
                        String uploadDir = "src/main/resources/static/uploads/";
                        // Tạo thư mục nếu chưa tồn tại
                        java.io.File dir = new java.io.File(uploadDir);
                        if (!dir.exists())
                            dir.mkdirs();

                        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                        String filePath = uploadDir + fileName;

                        // Copy file
                        java.nio.file.Files.copy(file.getInputStream(), java.nio.file.Paths.get(filePath),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                        // Tạo Entity
                        com.hcmute.qaute.entity.Attachment attachment = new com.hcmute.qaute.entity.Attachment();
                        attachment.setFilename(file.getOriginalFilename());
                        attachment.setStoragePath("/uploads/" + fileName);
                        attachment.setFileSize(file.getSize());
                        attachment.setContentType(file.getContentType());
                        attachment.setUploadedBy(student);
                        attachment.setQuestion(saved);

                        attachmentRepository.save(attachment);

                    } catch (Exception e) {
                        e.printStackTrace();
                        // Chấp nhận lỗi file lẻ, không rollback để tránh mất câu hỏi
                    }
                }
            }
        }
        // -----------------------------

        return mapToDTO(saved);
    }

    @Override
    public List<QuestionResponseDTO> getMyQuestions(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Question> list = questionRepository.findByStudentId(student.getId());
        if (list != null) {
            list.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
        }
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponseDTO> getQuestionsForDashboard(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Question> list = new ArrayList<>();
        String roleCode = currentUser.getRole().getCode();

        if ("ADMIN".equals(roleCode)) {
            list = questionRepository.findAll();
        } else if ("ADVISOR".equals(roleCode)) {
            if (currentUser.getDepartment() != null) {
                Integer deptId = currentUser.getDepartment().getId();
                // Use default sort for dashboard
                org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0,
                        100, org.springframework.data.domain.Sort.by("createdAt").descending());
                list = questionRepository.findByDepartmentId(deptId, pageable).getContent();
            } else {
                list = new ArrayList<>();
            }
        }

        if (list != null && !list.isEmpty()) {
            List<Question> mutableList = new ArrayList<>(list);
            mutableList.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
            list = mutableList;
        }

        return list.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponseDTO> getAllQuestions() {
        List<Question> list = questionRepository.findAll();
        if (list != null && !list.isEmpty()) {
            list.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
        }
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public QuestionResponseDTO getQuestionDetail(Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi ID: " + id));
        return mapToDTO(q);
    }

    @Override
    public List<QuestionResponseDTO> searchQuestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        org.springframework.data.domain.Page<Question> pageResult = questionRepository.searchByKeyword(keyword,
                org.springframework.data.domain.PageRequest.of(0, 50));

        List<Question> list = pageResult.getContent();
        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponseDTO> getFilterQuestions(Integer departmentId, String sort, String tag) {
        org.springframework.data.domain.Pageable pageable;

        // 1. Xác định Sort
        if ("popular".equalsIgnoreCase(sort)) {
            pageable = org.springframework.data.domain.PageRequest.of(0, 50,
                    org.springframework.data.domain.Sort.by("viewCount").descending());
        } else if ("unanswered".equalsIgnoreCase(sort)) {
            pageable = org.springframework.data.domain.PageRequest.of(0, 50,
                    org.springframework.data.domain.Sort.by("createdAt").ascending());
        } else {
            // Mặc định: Mới nhất
            pageable = org.springframework.data.domain.PageRequest.of(0, 50,
                    org.springframework.data.domain.Sort.by("createdAt").descending());
        }

        List<Question> list;
        org.springframework.data.domain.Page<Question> pageResult;

        // 2. Query
        if (departmentId != null) {
            pageResult = questionRepository.findByDepartmentId(departmentId, pageable);
            list = pageResult.getContent();
        } else {
            pageResult = questionRepository.findAll(pageable);
            list = new ArrayList<>(pageResult.getContent());
        }

        // 3. Filter thêm

        // Filter by Tag
        if (tag != null && !tag.isEmpty()) {
            list = list.stream()
                    .filter(q -> q.getTagsCached() != null && q.getTagsCached().contains(tag))
                    .collect(Collectors.toList());
        }

        // Filter by Status if Sort is 'unanswered'
        if ("unanswered".equalsIgnoreCase(sort)) {
            list = list.stream()
                    .filter(q -> q.getStatus() == QuestionStatus.PENDING)
                    .collect(Collectors.toList());
        }

        return list.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // --- HÀM CHUYỂN ĐỔI ENTITY -> DTO (QUAN TRỌNG) ---
    private QuestionResponseDTO mapToDTO(Question q) {
        QuestionResponseDTO dto = new QuestionResponseDTO();
        dto.setId(q.getId());
        dto.setTitle(q.getTitle());
        dto.setContent(q.getContent());
        dto.setPriority(q.getPriority());
        dto.setStatus(q.getStatus());
        dto.setCreatedAt(q.getCreatedAt());
        dto.setTimeAgo(calculateTimeAgo(q.getCreatedAt()));
        dto.setViewCount(q.getViewCount() == null ? 0 : q.getViewCount()); // Map viewCount

        dto.setStudentFaculty(q.getStudentFaculty());
        dto.setStudentCohort(q.getStudentCohort());

        // Mapping thông tin sinh viên
        if (q.getStudent() != null) {
            dto.setStudentId(q.getStudent().getId());
            dto.setStudentName(q.getStudent().getFullName());
            dto.setStudentAvatar(q.getStudent().getAvatarUrl());

            // --- [ĐÃ BỔ SUNG] Lấy MSSV thật để hiện lên Dashboard ---
            dto.setStudentIdCode(q.getStudent().getStudentIdCode());
            // --------------------------------------------------------
        }

        if (q.getDepartment() != null) {
            dto.setDepartmentId(q.getDepartment().getId());
            dto.setDepartmentName(q.getDepartment().getName());
        }

        // --- MAPPING MỚI (TAGS & ATTACHMENTS) ---
        dto.setTagsCached(q.getTagsCached());
        dto.setAttachments(q.getAttachments());
        // ----------------------------------------

        return dto;

    }

    private String calculateTimeAgo(LocalDateTime createdAt) {
        if (createdAt == null)
            return "Vừa xong";
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createdAt, now);
        long seconds = duration.getSeconds();
        if (seconds < 60)
            return "Vừa xong";
        else if (seconds < 3600)
            return (seconds / 60) + " phút trước";
        else if (seconds < 86400)
            return (seconds / 3600) + " giờ trước";
        else
            return (seconds / 86400) + " ngày trước";
    }
}