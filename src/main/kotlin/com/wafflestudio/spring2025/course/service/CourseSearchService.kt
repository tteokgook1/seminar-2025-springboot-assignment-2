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
        val allCourses = courseRepository.findAll()
            .filter { course ->
                (year == null || course.year == year) &&
                        (semester == null || course.semester == semester) &&
                        (
                                keyword.isNullOrBlank() ||
                                        course.courseTitle.contains(keyword, ignoreCase = true) ||
                                        (course.professor?.contains(keyword, ignoreCase = true) ?: false)
                                )
            }
            .sortedBy { it.id }

        val afterCursor = allCourses.filter { (it.id ?: 0) > nextId }
        val chunk = afterCursor.take(size + 1)
        val hasNext = chunk.size > size

        val limited = chunk.take(size)
        val lastId = limited.lastOrNull()?.id ?: nextId

        val courseIds = limited.mapNotNull { it.id }
        val classTimesByCourseId = if (courseIds.isNotEmpty()) {
            classTimeRepository.findAllByCourseIdIn(courseIds)
                .groupBy { it.courseId }
        } else {
            emptyMap()
        }

        return CourseSearchResponse(
            items = limited.map { course ->
                val classTimes = classTimesByCourseId[course.id!!] ?: emptyList()
                CourseDTO(course, classTimes)
            },
            meta = CourseSearchResponse.PaginationMeta(
                nextId = lastId,
                hasNext = hasNext,
            ),
        )
    }
}
