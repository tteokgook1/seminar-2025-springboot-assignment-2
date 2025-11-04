package com.wafflestudio.spring2025.timetable.dto
import jakarta.validation.constraints.NotNull

data class TimetableAddCourseRequest(
    @field:NotNull
    val courseId: Long,
)