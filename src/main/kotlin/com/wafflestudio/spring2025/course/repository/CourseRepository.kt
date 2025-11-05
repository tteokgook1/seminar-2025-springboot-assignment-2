package com.wafflestudio.spring2025.course.repository

import com.wafflestudio.spring2025.course.model.Course
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface CourseRepository : CrudRepository<Course, Long> {
    @Query(
        """
        SELECT * FROM courses
        WHERE
            (:year IS NULL OR year = :year) AND
            (:semester IS NULL OR semester = :semester) AND
            (:keywordPattern IS NULL OR (
                course_title LIKE :keywordPattern OR
                professor LIKE :keywordPattern
            )) AND
            id > :nextId
        ORDER BY id ASC
        LIMIT :limit
        """,
    )
    fun searchWithCursor(
        @Param("year") year: Int?,
        @Param("semester") semester: Int?,
        @Param("keywordPattern") keywordPattern: String?,
        @Param("nextId") nextId: Long,
        @Param("limit") limit: Int,
    ): List<Course>
}
