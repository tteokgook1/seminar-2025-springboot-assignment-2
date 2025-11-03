package com.wafflestudio.spring2025.course.controller

import com.wafflestudio.spring2025.course.service.SugangSnuFetchService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/courses/fetch")
class CourseFetchController(
    private val sugangSnuFetchService: SugangSnuFetchService
) {
    @PostMapping
    fun fetchCourses(
        @RequestParam year: Int,
        @RequestParam semester: Int
    ) {
        sugangSnuFetchService.fetchAndSaveCourses(year, semester)
    }
}
