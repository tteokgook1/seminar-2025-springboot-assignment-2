package com.wafflestudio.spring2025.timetable.dto

import jakarta.validation.constraints.NotBlank

data class TimetablesUpdateRequest (
    @field:NotBlank(message = "User ID cannot be blank")
    val name: String
)