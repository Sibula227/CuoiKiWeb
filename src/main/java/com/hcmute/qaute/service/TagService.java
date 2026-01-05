package com.hcmute.qaute.service;

import com.hcmute.qaute.entity.Tag;
import java.util.List;
import java.util.Set;

public interface TagService {
    Set<Tag> processTags(String tagInput);

    List<Tag> getAllTags();

    List<Tag> getPopularTags();
}
