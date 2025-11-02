package com.wafflestudio.spring2025.course.dto.core

import com.wafflestudio.spring2025.course.model.ClassTime

data class ClassTimeDTO(
    var id: Long,
    var courseId: Long,
    var dayOfWeek: Int,
    var startMinute: Int,
    var endMinute: Int,
    var location: String? = null,
) {
    constructor(ct: ClassTime) : this(
        ct.id!!,
        ct.courseId,
        ct.dayOfWeek,
        ct.startMinute,
        ct.endMinute,
        ct.location,
    )
}
