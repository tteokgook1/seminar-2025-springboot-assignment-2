package com.wafflestudio.spring2025.timetable

class ForbiddenException(
    message: String,
) : RuntimeException(message)

// 404 Not Found
class NotFoundException(
    message: String,
) : RuntimeException(message)

// 409 Conflict
class ConflictException(
    message: String,
) : RuntimeException(message)
