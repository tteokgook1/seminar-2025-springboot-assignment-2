package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.TimetableCourse
import org.springframework.data.repository.CrudRepository

interface TimetableCourseRepository : CrudRepository<TimetableCourse, Long>
