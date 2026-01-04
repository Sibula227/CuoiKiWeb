package com.hcmute.qaute.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.io.File;

@Service
public class KnowledgeService {

    private String knowledgeBase;

    @PostConstruct
    public void init() {
        try {
            // Path to the data directory - Adjust relative path if needed or use absolute
            // for now as we are local
            // Using system property user.dir usually points to project root
            Path dataDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "com", "hcmute", "qaute",
                    "data");

            if (Files.exists(dataDir) && Files.isDirectory(dataDir)) {
                try (Stream<Path> paths = Files.walk(dataDir)) {
                    knowledgeBase = paths
                            .filter(Files::isRegularFile)
                            .filter(p -> p.toString().endsWith(".md"))
                            .map(this::readFileContent)
                            .collect(Collectors.joining("\n\n"));
                }
            } else {
                knowledgeBase = "No data found.";
                System.err.println("Data directory not found: " + dataDir.toAbsolutePath());
            }

        } catch (IOException e) {
            e.printStackTrace();
            knowledgeBase = "Error loading data.";
        }
    }

    private String readFileContent(Path path) {
        try {
            return "--- File: " + path.getFileName() + " ---\n" + Files.readString(path);
        } catch (IOException e) {
            return "";
        }
    }

    public String getKnowledgeBase() {
        return knowledgeBase;
    }
}
