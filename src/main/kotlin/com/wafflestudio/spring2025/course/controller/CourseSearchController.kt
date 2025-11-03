package com.wafflestudio.spring2025.course.controller

import com.wafflestudio.spring2025.course.service.CourseSearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/courses")
class CourseSearchController(
    private val courseSearchService: CourseSearchService
) {
    @GetMapping("/search")
    fun searchCourses(
        @RequestParam year: Int,
        @RequestParam semester: Int,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ) = courseSearchService.searchCourses(year, semester, keyword, page, size)
}
