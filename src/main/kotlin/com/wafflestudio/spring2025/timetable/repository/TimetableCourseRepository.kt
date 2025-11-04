package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.TimetableCourse
import org.springframework.data.repository.CrudRepository
import org.springframework.transaction.annotation.Transactional

interface TimetableCourseRepository : CrudRepository<TimetableCourse, Long> {
    fun deleteAllByTimetableId(timetableId: Long)
    fun findAllByTimetableId(timetableId: Long): List<TimetableCourse>
    fun existsByTimetableIdAndCourseId(timetableId: Long, courseId: Long): Boolean
    @Transactional
    fun deleteByTimetableIdAndCourseId(timetableId: Long, courseId: Long)
}
