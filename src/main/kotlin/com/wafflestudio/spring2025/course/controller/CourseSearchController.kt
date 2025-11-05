package com.wafflestudio.spring2025.course.controller

import com.wafflestudio.spring2025.course.dto.CourseSearchResponse
import com.wafflestudio.spring2025.course.service.CourseSearchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/courses")
class CourseSearchController(
    private val courseSearchService: CourseSearchService,
) {
    @GetMapping
    fun searchCourses(
        @RequestParam(required = false) year: Int?,
        @RequestParam(required = false) semester: Int?,
        @RequestParam(required = false, name = "q") keyword: String?,
        @RequestParam(required = false, defaultValue = "0") nextId: Long,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<CourseSearchResponse> =
        ResponseEntity.ok(
            courseSearchService.searchCourses(year, semester, keyword, nextId, size),
        )
}
