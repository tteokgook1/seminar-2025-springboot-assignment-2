package com.wafflestudio.spring2025.course.model

data class CourseWithClassTime(
    var id: Long,
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
    var classTimes: List<ClassTime>,
) {
    data class ClassTime(
        var id: Long,
        var courseId: Long,
        var dayOfWeek: Int,
        var startMinute: Int,
        var endMinute: Int,
        var location: String? = null,
    )
}
