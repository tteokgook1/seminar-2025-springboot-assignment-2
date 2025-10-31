package com.wafflestudio.spring2025.timetable.dto.core

import com.wafflestudio.spring2025.course.dto.core.CourseDTO

data class TimetableDetailsDTO(
    var timetable: TimetableDTO,
    var courses: List<CourseDTO>,
    var credits: Int,
) {
    constructor(timetable: TimetableDTO, courses: List<CourseDTO>) : this(
        timetable,
        courses,
        courses.sumOf { it.credits },
    )
}
