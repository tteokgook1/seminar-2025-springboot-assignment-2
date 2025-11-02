package com.wafflestudio.spring2025.course.repository

import com.wafflestudio.spring2025.course.model.ClassTime
import org.springframework.data.repository.CrudRepository

interface ClassTimeRepository : CrudRepository<ClassTime, Long>
