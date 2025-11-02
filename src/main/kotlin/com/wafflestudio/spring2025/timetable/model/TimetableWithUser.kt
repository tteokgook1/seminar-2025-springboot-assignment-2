package com.wafflestudio.spring2025.timetable.model

import org.springframework.data.relational.core.mapping.Embedded
import java.time.Instant

data class TimetableWithUser(
    var id: Long,
    @Embedded.Nullable(prefix = "user_")
    val user: User?,
    var name: String,
    var year: Int,
    var semester: Int,
    var createdAt: Instant,
    var updatedAt: Instant,
) {
    data class User(
        val id: Long,
        val username: String,
    )
}
