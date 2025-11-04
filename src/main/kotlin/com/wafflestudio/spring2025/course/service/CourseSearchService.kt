package com.wafflestudio.spring2025.course.service

import com.wafflestudio.spring2025.course.dto.CourseSearchResponse
import com.wafflestudio.spring2025.course.dto.core.CourseDTO
import com.wafflestudio.spring2025.course.repository.CourseRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class CourseSearchService(
    private val courseRepository: CourseRepository,
) {
    fun searchCourses(
        year: Int,
        semester: Int,
        keyword: String?,
        page: Int,
        size: Int,
    ): CourseSearchResponse {
        val pageable = PageRequest.of(page, size)
        val courses =
            courseRepository
                .findAll()
                .filter {
                    it.year == year &&
                        it.semester == semester &&
                        (
                            keyword.isNullOrBlank() ||
                                it.courseTitle.contains(keyword, ignoreCase = true) ||
                                (it.professor?.contains(keyword, ignoreCase = true) ?: false)
                        )
                }.take(size)

        val lastId = courses.lastOrNull()?.id ?: 0
        return CourseSearchResponse(
            items = courses.map { CourseDTO(it, emptyList()) },
            meta =
                CourseSearchResponse.PaginationMeta(
                    nextId = lastId,
                    hasNext = courses.size == size,
                ),
        )
    }
}
