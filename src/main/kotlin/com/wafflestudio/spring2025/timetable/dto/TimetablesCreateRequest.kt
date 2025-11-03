package com.wafflestudio.spring2025.timetable.dto
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Range

data class TimetablesCreateRequest(
    @field:NotBlank(message = "이름은 공백일 수 없습니다.")
    val name: String,
    val year: Int,
    @field:Range(min = 1, max = 4, message = "학기는 1, 2, 3, 4 중 하나여야 합니다.")
    val semester: Int,
)
