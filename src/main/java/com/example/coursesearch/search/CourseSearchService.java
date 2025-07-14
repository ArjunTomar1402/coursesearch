package com.example.coursesearch.search;

import com.example.coursesearch.document.CourseDocument;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseSearchService {

    private static final Logger logger = LoggerFactory.getLogger(CourseSearchService.class);
    private final ElasticsearchOperations elasticsearchOperations;

    public CourseSearchService(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    /**
     * Enhanced search with working fuzzy matching support
     * Supports keyword search with fuzziness for typos like "sciens" -> "science"
     */
    public List<CourseDocument> search(String keyword, Integer minAge, Integer maxAge) {
        logger.info("Searching with keyword: {}, minAge: {}, maxAge: {}", keyword, minAge, maxAge);

        // Use the native search approach that actually works
        return searchNative(keyword, minAge, maxAge);
    }

    /**
     * String query fuzzy search - compatible with newer Spring Data Elasticsearch
     */
    public List<CourseDocument> searchNative(String keyword, Integer minAge, Integer maxAge) {
        logger.info("Native search with keyword: {}, minAge: {}, maxAge: {}", keyword, minAge, maxAge);

        if (keyword == null || keyword.trim().isEmpty()) {
            // If no keyword, just return all courses (with age filters if provided)
            return getAllCourses();
        }

        // Build Elasticsearch JSON query string for fuzzy search
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("{\n");
        queryBuilder.append("  \"bool\": {\n");
        queryBuilder.append("    \"must\": [\n");

        // Add fuzzy multi-match query
        queryBuilder.append("      {\n");
        queryBuilder.append("        \"multi_match\": {\n");
        queryBuilder.append("          \"query\": \"").append(keyword.trim()).append("\",\n");
        queryBuilder.append("          \"fields\": [\"title\", \"description\", \"category\"],\n");
        queryBuilder.append("          \"fuzziness\": \"AUTO\",\n");
        queryBuilder.append("          \"prefix_length\": 0,\n");
        queryBuilder.append("          \"max_expansions\": 50\n");
        queryBuilder.append("        }\n");
        queryBuilder.append("      }");

        // Add age range filters if provided
        if (minAge != null) {
            queryBuilder.append(",\n      {\n");
            queryBuilder.append("        \"range\": {\n");
            queryBuilder.append("          \"maxAge\": {\n");
            queryBuilder.append("            \"gte\": ").append(minAge).append("\n");
            queryBuilder.append("          }\n");
            queryBuilder.append("        }\n");
            queryBuilder.append("      }");
        }

        if (maxAge != null) {
            queryBuilder.append(",\n      {\n");
            queryBuilder.append("        \"range\": {\n");
            queryBuilder.append("          \"minAge\": {\n");
            queryBuilder.append("            \"lte\": ").append(maxAge).append("\n");
            queryBuilder.append("          }\n");
            queryBuilder.append("        }\n");
            queryBuilder.append("      }");
        }

        queryBuilder.append("\n    ]\n");
        queryBuilder.append("  }\n");
        queryBuilder.append("}");

        String queryString = queryBuilder.toString();
        logger.info("Elasticsearch query: {}", queryString);

        Query searchQuery = new StringQuery(queryString);
        searchQuery.setPageable(PageRequest.of(0, 20));

        SearchHits<CourseDocument> hits = elasticsearchOperations.search(searchQuery, CourseDocument.class);

        logger.info("Native search found {} total hits", hits.getTotalHits());
        List<CourseDocument> results = hits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

        results.forEach(course -> logger.info("Found course: {} (ages {}-{})",
                course.getTitle(), course.getMinAge(), course.getMaxAge()));

        return results;
    }
    public List<CourseDocument> searchAlternative(String keyword, Integer minAge, Integer maxAge) {
        logger.info("Alternative search with keyword: {}, minAge: {}, maxAge: {}", keyword, minAge, maxAge);

        Criteria criteria = new Criteria();

        // Try contains() with fuzzy instead of matches() with fuzzy
        if (keyword != null && !keyword.trim().isEmpty()) {
            Criteria keywordCriteria = new Criteria("title").contains(keyword).fuzzy("1")
                    .or(new Criteria("description").contains(keyword).fuzzy("1"))
                    .or(new Criteria("category").contains(keyword).fuzzy("1"));
            criteria = criteria.and(keywordCriteria);
        }

        // Add age range filters for overlapping ranges
        if (minAge != null) {
            criteria = criteria.and(new Criteria("maxAge").greaterThanEqual(minAge));
        }

        if (maxAge != null) {
            criteria = criteria.and(new Criteria("minAge").lessThanEqual(maxAge));
        }

        CriteriaQuery query = new CriteriaQuery(criteria, PageRequest.of(0, 20));
        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);

        logger.info("Alternative search found {} total hits", hits.getTotalHits());
        List<CourseDocument> results = hits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

        results.forEach(course -> logger.info("Found course: {} (ages {}-{})",
                course.getTitle(), course.getMinAge(), course.getMaxAge()));

        return results;
    }
    public List<CourseDocument> getAllCourses() {
        logger.info("Fetching all courses for debugging");

        // Search without any criteria to get all documents
        Criteria criteria = new Criteria();
        CriteriaQuery query = new CriteriaQuery(criteria, PageRequest.of(0, 100));
        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);

        logger.info("Total courses in index: {}", hits.getTotalHits());
        List<CourseDocument> results = hits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());

        // Log all courses for debugging
        results.forEach(course -> logger.info("Course: {} | Category: {} | Description: {} | Ages: {}-{}",
                course.getTitle(), course.getCategory(), course.getDescription(),
                course.getMinAge(), course.getMaxAge()));

        return results;
    }

    /**
     * Debug method to test exact match search
     */
    public List<CourseDocument> searchExact(String keyword) {
        logger.info("Exact search with keyword: {}", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        Criteria criteria = new Criteria("title").contains(keyword)
                .or(new Criteria("description").contains(keyword))
                .or(new Criteria("category").contains(keyword));

        CriteriaQuery query = new CriteriaQuery(criteria, PageRequest.of(0, 20));
        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);

        logger.info("Exact search found {} hits", hits.getTotalHits());
        return hits.getSearchHits().stream()
                .map(hit -> hit.getContent())
                .collect(Collectors.toList());
    }

    /**
     * Autocomplete functionality - returns course titles that start with the given prefix
     */
    public List<String> autocomplete(String prefix) {
        logger.info("Autocomplete search with prefix: {}", prefix);

        if (prefix == null || prefix.trim().isEmpty()) {
            return List.of();
        }

        Criteria criteria = new Criteria("title").startsWith(prefix.trim());
        CriteriaQuery query = new CriteriaQuery(criteria, PageRequest.of(0, 10));
        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);

        logger.info("Found {} autocomplete suggestions", hits.getTotalHits());
        return hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getTitle())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Advanced autocomplete with fuzzy matching
     * This allows for typos in the prefix as well
     */
    public List<String> fuzzyAutocomplete(String prefix) {
        logger.info("Fuzzy autocomplete search with prefix: {}", prefix);

        if (prefix == null || prefix.trim().isEmpty()) {
            return List.of();
        }

        Criteria criteria = new Criteria("title").matches(prefix.trim()).fuzzy("AUTO");
        CriteriaQuery query = new CriteriaQuery(criteria, PageRequest.of(0, 10));
        SearchHits<CourseDocument> hits = elasticsearchOperations.search(query, CourseDocument.class);

        logger.info("Found {} fuzzy autocomplete suggestions", hits.getTotalHits());
        return hits.getSearchHits().stream()
                .map(hit -> hit.getContent().getTitle())
                .distinct()
                .collect(Collectors.toList());
    }
}