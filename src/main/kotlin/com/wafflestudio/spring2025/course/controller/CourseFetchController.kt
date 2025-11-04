package com.wafflestudio.spring2025.course.controller

import com.wafflestudio.spring2025.course.service.SugangSnuFetchService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/courses/fetch")
class CourseFetchController(
    private val sugangSnuFetchService: SugangSnuFetchService,
) {
    @PostMapping
    fun fetchCourses(
        @RequestParam year: Int,
        @RequestParam semester: Int,
    ) {
        sugangSnuFetchService.fetchAndSaveCourses(year, semester)
    }
}
