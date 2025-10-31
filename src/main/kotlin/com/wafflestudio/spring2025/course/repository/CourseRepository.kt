package com.wafflestudio.spring2025.course.repository

import com.wafflestudio.spring2025.course.model.Course
import org.springframework.data.repository.CrudRepository

interface CourseRepository : CrudRepository<Course, Long>
