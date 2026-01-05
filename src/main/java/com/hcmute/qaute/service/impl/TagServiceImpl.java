package com.hcmute.qaute.service.impl;

import com.hcmute.qaute.entity.Tag;
import com.hcmute.qaute.repository.TagRepository;
import com.hcmute.qaute.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagRepository tagRepository;

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Override
    @Transactional
    public Set<Tag> processTags(String tagInput) {
        Set<Tag> tags = new HashSet<>();
        if (tagInput == null || tagInput.trim().isEmpty()) {
            return tags;
        }

        String[] tagNames = tagInput.split(",");
        for (String tagName : tagNames) {
            String cleanName = tagName.trim();
            if (cleanName.isEmpty())
                continue;

            String slug = toSlug(cleanName);

            // Tìm xem tag đã có chưa, nếu chưa thì tạo mới
            Tag tag = tagRepository.findBySlug(slug)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(cleanName);
                        newTag.setSlug(slug);
                        return tagRepository.save(newTag);
                    });
            tags.add(tag);
        }
        return tags;
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public List<Tag> getPopularTags() {
        // Tạm thời lấy top 10 tag created gần nhất, sau này có thể count usage
        // Thực tế nên query count trong question_tags
        return tagRepository.findAll().stream().limit(10).toList();
    }

    public String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
}
