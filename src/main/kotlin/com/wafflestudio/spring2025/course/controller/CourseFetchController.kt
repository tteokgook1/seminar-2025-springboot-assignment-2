package com.wafflestudio.spring2025.course.controller

import com.wafflestudio.spring2025.course.service.SugangSnuFetchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

// @RequestMapping("/api/v1/courses/fetch")
@RestController
@RequestMapping("/api/v1/courses")
class CourseFetchController(
    private val sugangSnuFetchService: SugangSnuFetchService,
) {
    @PostMapping
    fun fetchCourses(
        @RequestParam year: Int,
        @RequestParam semester: Int,
    ): ResponseEntity<Unit> { // ⬅️ 반환 타입 명시
        sugangSnuFetchService.fetchAndSaveCourses(year, semester)
        // 테스트에서 isNoContent (204)를 기대합니다.
        return ResponseEntity.noContent().build()
    }
    // ) {
    //    sugangSnuFetchService.fetchAndSaveCourses(year, semester)
    // }
}
