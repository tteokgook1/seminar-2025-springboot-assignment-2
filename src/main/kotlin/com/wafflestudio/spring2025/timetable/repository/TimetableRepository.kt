package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.Timetable
import org.springframework.data.repository.CrudRepository

interface TimetableRepository : CrudRepository<Timetable, Long>
