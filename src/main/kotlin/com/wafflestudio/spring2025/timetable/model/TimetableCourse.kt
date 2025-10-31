package com.wafflestudio.spring2025.timetable.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("timetable_courses")
class TimetableCourse(
    @Id var id: Long? = null,
    var timetableId: Long,
    var courseId: Long,
)
