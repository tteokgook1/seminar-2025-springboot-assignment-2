package com.wafflestudio.spring2025.course.dto

import com.wafflestudio.spring2025.course.dto.core.CourseDTO

data class CourseSearchResponse(
    val items: List<CourseDTO>,
    val meta: PaginationMeta,
) {
    data class PaginationMeta(
        val nextId: Long,
        val hasNext: Boolean,
    )
}
