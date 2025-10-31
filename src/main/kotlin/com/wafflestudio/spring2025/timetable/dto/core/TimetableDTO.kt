package com.wafflestudio.spring2025.timetable.dto.core

import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.model.TimetableWithUser
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User
import java.time.Instant

data class TimetableDTO(
    var id: Long,
    var user: UserDto,
    var name: String,
    var year: Int,
    var semester: Int,
    var createdAt: Instant,
    var updatedAt: Instant,
) {
    constructor(tt: Timetable, u: User) : this(
        tt.id!!,
        UserDto(u),
        tt.name,
        tt.year,
        tt.semester,
        tt.createdAt!!,
        tt.updatedAt!!,
    )

    constructor(tt: TimetableWithUser) : this(
        tt.id,
        UserDto(tt.user!!.id, tt.user.username),
        tt.name,
        tt.year,
        tt.semester,
        tt.createdAt,
        tt.updatedAt,
    )
}
