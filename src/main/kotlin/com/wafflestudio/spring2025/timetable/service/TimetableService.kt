package com.wafflestudio.spring2025.timetable.service

import com.wafflestudio.spring2025.timetable.ConflictException
import com.wafflestudio.spring2025.timetable.ForbiddenException
import com.wafflestudio.spring2025.timetable.NotFoundException
import com.wafflestudio.spring2025.timetable.dto.TimetablesCreateRequest
import com.wafflestudio.spring2025.timetable.dto.TimetablesUpdateRequest
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDTO
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.repository.TimetableCourseRepository
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import com.wafflestudio.spring2025.user.model.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimetableService(
    private val timetableRepository: TimetableRepository,
    private val timetableCourseRepository: TimetableCourseRepository,
) {
    @Transactional
    fun createTimetable(
        user: User,
        request: TimetablesCreateRequest,
    ): TimetableDTO {
        if (timetableRepository.existsByUserIdAndNameAndYearAndSemester(
                user.id!!,
                request.name,
                request.year,
                request.semester,
            )
        ) {
            throw ConflictException("이미 동일한 이름, 연도, 학기의 시간표가 존재합니다.")
        }
        val timetable =
            Timetable(
                userId = user.id!!,
                name = request.name,
                year = request.year,
                semester = request.semester,
            )
        val savedTimetable = timetableRepository.save(timetable)
        return TimetableDTO(savedTimetable, user)
    }

    @Transactional(readOnly = true)
    fun getTimetables(userId: Long): List<TimetableDTO> {
        val timetablesWithUser = timetableRepository.findAllWithUserByUserId(userId)
        return timetablesWithUser.map { TimetableDTO(it) }
    }

    @Transactional
    fun updateTimetable(
        user: User,
        id: Long,
        request: TimetablesUpdateRequest,
    ): TimetableDTO {
        val timetable =
            timetableRepository
                .findById(id)
                .orElseThrow { NotFoundException("해당 ID의 시간표를 찾을 수 없습니다.") }

        if (timetable.userId != user.id) {
            throw ForbiddenException("이 시간표를 수정할 권한이 없습니다.")
        }

        if (timetableRepository.existsByUserIdAndNameAndYearAndSemesterAndIdNot(
                user.id!!,
                request.name,
                timetable.year,
                timetable.semester,
                id,
            )
        ) {
            throw ConflictException("이미 동일한 이름의 시간표가 존재합니다.")
        }

        timetable.name = request.name
        val updatedTimetable = timetableRepository.save(timetable)
        return TimetableDTO(updatedTimetable, user)
    }

    @Transactional
    fun deleteTimetable(
        user: User,
        id: Long,
    ) {
        val timetable =
            timetableRepository
                .findById(id)
                .orElseThrow { NotFoundException("해당 ID의 시간표를 찾을 수 없습니다.") }

        if (timetable.userId != user.id) {
            throw ForbiddenException("이 시간표를 삭제할 권한이 없습니다.")
        }
        timetableCourseRepository.deleteAllByTimetableId(id)
        timetableRepository.delete(timetable)
    }
}
