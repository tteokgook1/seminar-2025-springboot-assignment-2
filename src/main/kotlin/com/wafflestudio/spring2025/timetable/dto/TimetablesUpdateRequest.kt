package com.wafflestudio.spring2025.timetable.dto

import jakarta.validation.constraints.NotBlank

data class TimetablesUpdateRequest(
    @field:NotBlank(message = "Name cannot be blank")
    val name: String,
)
