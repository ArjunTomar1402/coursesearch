package com.example.coursesearch.search;

import com.example.coursesearch.document.CourseDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

//import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataElasticsearchTest
public class CourseSearchServiceTest {

    @Autowired
    private ElasticsearchOperations operations;

    @BeforeEach
    void setUp() {
        IndexOperations indexOps = operations.indexOps(CourseDocument.class);
        if (indexOps.exists()) {
            indexOps.delete();
        }
        indexOps.create();
        indexOps.putMapping(indexOps.createMapping());

        List<CourseDocument> courses = List.of(
                CourseDocument.builder()
                        .id("1")
                        .title("Science Sparks")
                        .description("Fun experiments")
                        .category("Science")
                        .type("COURSE")
                        .gradeRange("2-3")
                        .minAge(7)
                        .maxAge(8)
                        .price(59.99)
                        .nextSessionDate(LocalDate.parse("2025-08-05"))
                .build(),

                CourseDocument.builder()
                        .id("2")
                        .title("Math Magic")
                        .description("Early math skills")
                        .category("Math")
                        .type("COURSE")
                        .gradeRange("1-2")
                        .minAge(5)
                        .maxAge(6)
                        .price(49.99)
                        .nextSessionDate(LocalDate.parse("2025-08-12"))
                        .build(),

                CourseDocument.builder()
                        .id("3")
                        .title("Teen Debates")
                        .description("Public speaking")
                        .category("Communication")
                        .type("CLUB")
                        .gradeRange("7-9")
                        .minAge(13)
                        .maxAge(15)
                        .price(179.0)
                        .nextSessionDate(LocalDate.parse("2025-08-12"))
                        .build()
        );

        // Save each document individually
        for (CourseDocument doc : courses) {
            operations.save(doc);
        }

        // Refresh the index to make documents searchable
        indexOps.refresh();
    }

    @Test
    void testFindByCategory() {
        CriteriaQuery query = new CriteriaQuery(new Criteria("category").is("Math"));
        List<CourseDocument> result = operations.search(query, CourseDocument.class)
                .map(searchHit -> searchHit.getContent())
                .toList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Math Magic");
    }

    @Test
    void testFindByMinAge() {
        CriteriaQuery query = new CriteriaQuery(new Criteria("minAge").greaterThanEqual(13));
        List<CourseDocument> result = operations.search(query, CourseDocument.class)
                .map(hit -> hit.getContent())
                .toList();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("Communication");
    }

    @Test
    void testFindAllCourses() {
        CriteriaQuery query = new CriteriaQuery(new Criteria());
        List<CourseDocument> result = operations.search(query, CourseDocument.class)
                .map(hit -> hit.getContent())
                .toList();

        assertThat(result).hasSize(3);
    }
}
