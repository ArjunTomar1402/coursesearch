package com.example.coursesearch.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;


import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "courses")
public class CourseDocument {

    @Id
    @NotBlank
    private String id;

    @Field(type = FieldType.Text)
    @NotBlank
    private String title;

    @Field(type = FieldType.Text)
    @NotBlank
    private String description;

    @Field(type = FieldType.Keyword)
    @NotBlank
    private String category;

    @Field(type = FieldType.Keyword)
    @NotBlank
    private String type; // ONE_TIME, COURSE, CLUB

    @Field(type = FieldType.Integer)
    @NotNull
    @Min(0)
    private Integer minAge;

    @Field(type = FieldType.Integer)
    @NotNull
    @Min(0)
    private Integer maxAge;

    @Field(type = FieldType.Double)
    @NotNull
    @PositiveOrZero
    private Double price;

    @Field(type = FieldType.Date, format = DateFormat.date)
    @NotNull
    private LocalDate nextSessionDate;

    @Field(type = FieldType.Keyword)
    @NotBlank
    private String gradeRange; // optional: e.g. "3-5", "6-8"
}
