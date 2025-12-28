package com.hcmute.qaute.repository;

import com.hcmute.qaute.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    Optional<Tag> findByName(String name);
    Optional<Tag> findBySlug(String slug);
}