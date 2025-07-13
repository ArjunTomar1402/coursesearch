package com.example.coursesearch.service;

import com.example.coursesearch.document.CourseDocument;
import com.example.coursesearch.repository.CourseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataIndexingService {

    private final CourseRepository courseRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void loadData() {
        try {
            long count = courseRepository.count();
            System.out.println("Courses in index before indexing: " + count);

            if (count == 0) {
                InputStream inputStream = new ClassPathResource("sample-courses.json").getInputStream();
                List<CourseDocument> courses = objectMapper.readValue(inputStream, new TypeReference<>() {});
                System.out.println("Courses loaded from JSON: " + courses.size());

                for (CourseDocument course : courses) {
                    try {
                        courseRepository.save(course);
                        System.out.println("✅ Indexed: " + course.getTitle());
                    } catch (Exception ex) {
                        System.err.println("❌ Failed to index course ID " + course.getId() + ": " + ex.getMessage());
                    }
                }

                System.out.println("✅ All courses processed.");
            } else {
                System.out.println("Index already populated.");
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to load or parse JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

