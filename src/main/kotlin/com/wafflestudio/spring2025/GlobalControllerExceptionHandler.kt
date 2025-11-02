package com.wafflestudio.spring2025

import com.wafflestudio.spring2025.timetable.NotFoundException
import com.wafflestudio.spring2025.timetable.ForbiddenException
import com.wafflestudio.spring2025.timetable.ConflictException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(exception: NotFoundException): ResponseEntity<Map<String, Any>> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND) // 404
            .body(mapOf("error" to (exception.message ?: "Not Found"), "errorCode" to "NOT_FOUND"))

    // 403 Forbidden 처리
    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(exception: ForbiddenException): ResponseEntity<Map<String, Any>> =
        ResponseEntity
            .status(HttpStatus.FORBIDDEN) // 403
            .body(mapOf("error" to (exception.message ?: "Forbidden"), "errorCode" to "FORBIDDEN"))

    // 409 Conflict 처리
    @ExceptionHandler(ConflictException::class)
    fun handleConflict(exception: ConflictException): ResponseEntity<Map<String, Any>> =
        ResponseEntity
            .status(HttpStatus.CONFLICT) // 409
            .body(mapOf("error" to (exception.message ?: "Conflict"), "errorCode" to "CONFLICT"))

    @ExceptionHandler(DomainException::class)
    fun handle(exception: DomainException): ResponseEntity<Map<String, Any>> =
        ResponseEntity
            .status(exception.httpErrorCode)
            .body(mapOf("error" to exception.msg, "errorCode" to exception.errorCode))
}
