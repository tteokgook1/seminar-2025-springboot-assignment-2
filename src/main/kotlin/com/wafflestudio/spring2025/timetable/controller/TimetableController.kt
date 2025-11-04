package com.wafflestudio.spring2025.timetable.controller

import com.wafflestudio.spring2025.timetable.dto.TimetableAddCourseRequest
import com.wafflestudio.spring2025.timetable.dto.TimetablesCreateRequest
import com.wafflestudio.spring2025.timetable.dto.TimetablesResponse
import com.wafflestudio.spring2025.timetable.dto.TimetablesUpdateRequest
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDTO
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDetailsDTO
import com.wafflestudio.spring2025.timetable.service.TimetableService
import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.model.User
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/timetables")
class TimetableController(
    private val timetableService: TimetableService,
) {
    @PostMapping
    fun createTimetable(
        @LoggedInUser user: User,
        @Valid @RequestBody request: TimetablesCreateRequest,
    ): ResponseEntity<TimetableDTO> {
        println(">>> Controller user = ${user.id}, ${user.username}")

        val timetableDto = timetableService.createTimetable(user, request)
        println("CREATE TIMETABLE => id=${timetableDto.id}, createdAt=${timetableDto.createdAt}, updatedAt=${timetableDto.updatedAt}")
        return ResponseEntity.status(HttpStatus.CREATED).body(timetableDto)
    }

    @GetMapping
    fun getTimetables(
        @LoggedInUser user: User,
    ): ResponseEntity<TimetablesResponse> {
        val timetables = timetableService.getTimetables(user.id!!)
        return ResponseEntity.ok(TimetablesResponse(timetables))
    }
    @GetMapping("/{id}")
    fun getTimetableDetails(
        @LoggedInUser user: User,
        @PathVariable id: Long,
    ): ResponseEntity<TimetableDetailsDTO> {
        val timetableDetails = timetableService.getTimetableDetails(user, id)
        return ResponseEntity.ok(timetableDetails)
    }

    @PatchMapping("/{id}")
    fun updateTimetable(
        @LoggedInUser user: User,
        @PathVariable id: Long,
        @Valid @RequestBody request: TimetablesUpdateRequest,
    ): ResponseEntity<TimetableDTO> {
        val timetableDto = timetableService.updateTimetable(user, id, request)
        return ResponseEntity.ok(timetableDto)
    }

    @DeleteMapping("/{id}")
    fun deleteTimetable(
        @LoggedInUser user: User,
        @PathVariable id: Long,
    ): ResponseEntity<Unit> {
        timetableService.deleteTimetable(user, id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/courses")
    fun addCourseToTimetable(
        @LoggedInUser user: User,
        @PathVariable id: Long,
        @Valid @RequestBody request: TimetableAddCourseRequest,
    ): ResponseEntity<TimetableDetailsDTO> {
        val timetableDetails = timetableService.addCourseToTimetable(user, id, request.courseId)
        return ResponseEntity.status(HttpStatus.CREATED).body(timetableDetails)
    }

    @DeleteMapping("/{id}/courses/{courseId}")
    fun removeCourseFromTimetable(
        @LoggedInUser user: User,
        @PathVariable id: Long,
        @PathVariable courseId: Long,
    ): ResponseEntity<Unit> {
        timetableService.removeCourseFromTimetable(user, id, courseId)
        return ResponseEntity.noContent().build()
    }
}
