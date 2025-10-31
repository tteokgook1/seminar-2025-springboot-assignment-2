package com.wafflestudio.spring2025.course.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("class_times")
class ClassTime(
    @Id var id: Long? = null,
    var courseId: Long,
    var dayOfWeek: Int,
    var startMinute: Int,
    var endMinute: Int,
    var location: String? = null,
)
