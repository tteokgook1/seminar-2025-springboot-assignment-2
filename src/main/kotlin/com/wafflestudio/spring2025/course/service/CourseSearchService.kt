package com.wafflestudio.spring2025.course.service

import com.wafflestudio.spring2025.course.dto.CourseSearchResponse
import com.wafflestudio.spring2025.course.dto.core.CourseDTO
import com.wafflestudio.spring2025.course.repository.ClassTimeRepository
import com.wafflestudio.spring2025.course.repository.CourseRepository
import org.springframework.stereotype.Service

@Service
class CourseSearchService(
    private val courseRepository: CourseRepository,
    private val classTimeRepository: ClassTimeRepository,
) {
    fun searchCourses(
        year: Int?,
        semester: Int?,
        keyword: String?,
        nextId: Long,
        size: Int,
    ): CourseSearchResponse {
        val limit = size + 1
        val keywordPattern = if (keyword.isNullOrBlank()) null else "%$keyword%"

        val courses =
            courseRepository.searchWithCursor(
                year = year,
                semester = semester,
                keywordPattern = keywordPattern,
                nextId = nextId,
                limit = limit,
            )

        val hasNext = courses.size > size
        val limited = courses.take(size)
        val lastId = limited.lastOrNull()?.id ?: nextId

        val courseIds = limited.mapNotNull { it.id }
        val classTimesByCourseId =
            if (courseIds.isNotEmpty()) {
                classTimeRepository
                    .findAllByCourseIdIn(courseIds)
                    .groupBy { it.courseId }
            } else {
                emptyMap()
            }

        return CourseSearchResponse(
            items =
                limited.map { course ->
                    val classTimes = classTimesByCourseId[course.id!!] ?: emptyList()
                    CourseDTO(course, classTimes)
                },
            meta =
                CourseSearchResponse.PaginationMeta(
                    nextId = lastId,
                    hasNext = hasNext,
                ),
        )
    }
}
