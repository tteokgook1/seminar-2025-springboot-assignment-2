package com.wafflestudio.spring2025.timetable.repository

import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.model.TimetableWithUser
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface TimetableRepository : CrudRepository<Timetable, Long>{
    @Query(
        """
        SELECT t.id, t.name, t.year, t.semester, t.created_at, t.updated_at,
               u.id as user_id, u.username as user_username
        FROM timetables t
        JOIN users u ON t.user_id = u.id
        WHERE t.user_id = :userId
        """
    )
    fun findAllWithUserByUserId(userId: Long): List<TimetableWithUser>

    fun existsByUserIdAndNameAndYearAndSemester(
        userId: Long,
        name: String,
        year: Int,
        semester: Int
    ): Boolean

    fun existsByUserIdAndNameAndYearAndSemesterAndIdNot(
        userId: Long,
        name: String,
        year: Int,
        semester: Int,
        id: Long
    ): Boolean



}
