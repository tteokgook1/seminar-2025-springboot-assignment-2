package com.wafflestudio.spring2025.course.dto.core

import com.wafflestudio.spring2025.course.model.ClassTime
import com.wafflestudio.spring2025.course.model.Course
import com.wafflestudio.spring2025.course.model.CourseWithClassTime

data class CourseDTO(
    var id: Long,
    var year: Int,
    var semester: Int,
    var classification: String?,
    var college: String?,
    var department: String?,
    var program: String?,
    var grade: Int?,
    var courseNumber: String,
    var lectureNumber: String,
    var courseTitle: String,
    var credits: Int,
    var professor: String?,
    var classTimes: List<ClassTimeDTO>,
) {
    constructor(course: Course, classTimes: List<ClassTime>) : this(
        course.id!!,
        course.year,
        course.semester,
        course.classification,
        course.college,
        course.department,
        course.program,
        course.grade,
        course.courseNumber,
        course.lectureNumber,
        course.courseTitle,
        course.credits,
        course.professor,
        classTimes.map { ClassTimeDTO(it) },
    )

    constructor(course: CourseWithClassTime) : this(
        course.id,
        course.year,
        course.semester,
        course.classification,
        course.college,
        course.department,
        course.program,
        course.grade,
        course.courseNumber,
        course.lectureNumber,
        course.courseTitle,
        course.credits,
        course.professor,
        course.classTimes.map {
            ClassTimeDTO(
                it.id,
                it.courseId,
                it.dayOfWeek,
                it.startMinute,
                it.endMinute,
                it.location,
            )
        },
    )
}
