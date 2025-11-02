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
    constructor(timetable: Timetable, user: User) : this(
        timetable.id!!,
        UserDto(user),
        timetable.name,
        timetable.year,
        timetable.semester,
        timetable.createdAt!!,
        timetable.updatedAt!!,
    )

    constructor(timetableWithUser: TimetableWithUser) : this(
        timetableWithUser.id,
        UserDto(timetableWithUser.user!!.id, timetableWithUser.user.username),
        timetableWithUser.name,
        timetableWithUser.year,
        timetableWithUser.semester,
        timetableWithUser.createdAt,
        timetableWithUser.updatedAt,
    )
}
