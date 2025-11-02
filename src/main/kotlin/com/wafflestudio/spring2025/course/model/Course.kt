package com.wafflestudio.spring2025.course.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("courses")
class Course(
    @Id var id: Long? = null,
    var year: Int,
    var semester: Int,
    var classification: String? = null,
    var college: String? = null,
    var department: String? = null,
    var program: String? = null,
    var grade: Int? = null,
    var courseNumber: String,
    var lectureNumber: String,
    var courseTitle: String,
    var credits: Int,
    var professor: String? = null,
)
