package com.wafflestudio.spring2025.timetable.service

import com.wafflestudio.spring2025.course.model.ClassTime
import com.wafflestudio.spring2025.course.dto.core.CourseDTO
import com.wafflestudio.spring2025.course.repository.ClassTimeRepository
import com.wafflestudio.spring2025.course.repository.CourseRepository
import com.wafflestudio.spring2025.timetable.ConflictException
import com.wafflestudio.spring2025.timetable.ForbiddenException
import com.wafflestudio.spring2025.timetable.NotFoundException
import com.wafflestudio.spring2025.timetable.dto.TimetablesCreateRequest
import com.wafflestudio.spring2025.timetable.dto.TimetablesUpdateRequest
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDTO
import com.wafflestudio.spring2025.timetable.dto.core.TimetableDetailsDTO
import com.wafflestudio.spring2025.timetable.model.Timetable
import com.wafflestudio.spring2025.timetable.model.TimetableCourse
import com.wafflestudio.spring2025.timetable.repository.TimetableCourseRepository
import com.wafflestudio.spring2025.timetable.repository.TimetableRepository
import com.wafflestudio.spring2025.user.model.User
import com.wafflestudio.spring2025.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TimetableService(
    private val timetableRepository: TimetableRepository,
    private val timetableCourseRepository: TimetableCourseRepository,
    private val userRepository: UserRepository,
    private val courseRepository: CourseRepository,
    private val classTimeRepository: ClassTimeRepository,
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

    @Transactional(readOnly = true)
    fun getTimetableDetails(
        user: User,
        id: Long,
    ): TimetableDetailsDTO {
        val timetable =
            timetableRepository
                .findById(id)
                .orElseThrow { NotFoundException("해당 ID의 시간표를 찾을 수 없습니다.") }

        if (timetable.userId != user.id) {
            throw ForbiddenException("이 시간표를 조회할 권한이 없습니다.")
        }

        val timetableCourses = timetableCourseRepository.findAllByTimetableId(id)
        val courseIds = timetableCourses.map { it.courseId }

        if (courseIds.isEmpty()) {
            return TimetableDetailsDTO(TimetableDTO(timetable, user), emptyList())
        }

        val courses = courseRepository.findAllById(courseIds).toList()

        val classTimes = classTimeRepository.findAllByCourseIdIn(courseIds)
        val classTimesByCourseId = classTimes.groupBy { it.courseId }

        val courseDTOs =
            courses.map { course ->
                CourseDTO(course, classTimesByCourseId[course.id] ?: emptyList())
            }

        return TimetableDetailsDTO(TimetableDTO(timetable, user), courseDTOs)
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

    @Transactional
    fun addCourseToTimetable(
        user: User,
        id: Long,
        courseId: Long,
    ): TimetableDetailsDTO {
        val timetable =
            timetableRepository
                .findById(id)
                .orElseThrow { NotFoundException("해당 ID의 시간표를 찾을 수 없습니다.") }

        if (timetable.userId != user.id) {
            throw ForbiddenException("이 시간표에 강의를 추가할 권한이 없습니다.")
        }

        val course =
            courseRepository
                .findById(courseId)
                .orElseThrow { NotFoundException("해당 ID의 강의를 찾을 수 없습니다.") }

        if (timetable.year != course.year || timetable.semester != course.semester) {
            throw ConflictException("시간표의 연도/학기와 강의의 연도/학기가 일치하지 않습니다.")
        }

        if (timetableCourseRepository.existsByTimetableIdAndCourseId(id, courseId)) {
            throw ConflictException("이미 시간표에 추가된 강의입니다.")
        }

        val newClassTimes = classTimeRepository.findAllByCourseId(courseId)
        val existingCourseIds = timetableCourseRepository.findAllByTimetableId(id).map { it.courseId }

        if (existingCourseIds.isNotEmpty()) {
            val existingClassTimes = classTimeRepository.findAllByCourseIdIn(existingCourseIds)
            if (checkTimeOverlap(existingClassTimes, newClassTimes)) {
                throw ConflictException("기존 시간표의 강의와 시간이 겹칩니다.")
            }
        }
        timetableCourseRepository.save(
            TimetableCourse(
                timetableId = id,
                courseId = courseId,
            ),
        )
        return getTimetableDetails(user, id)
    }

    @Transactional
    fun removeCourseFromTimetable(
        user: User,
        id: Long,
        courseId: Long,
    ) {
        val timetable =
            timetableRepository
                .findById(id)
                .orElseThrow { NotFoundException("해당 ID의 시간표를 찾을 수 없습니다.") }
        if (timetable.userId != user.id) {
            throw ForbiddenException("이 시간표의 강의를 삭제할 권한이 없습니다.")
        }

        if (!timetableCourseRepository.existsByTimetableIdAndCourseId(id, courseId)) {
            throw NotFoundException("시간표에 존재하지 않는 강의입니다.")
        }
        timetableCourseRepository.deleteByTimetableIdAndCourseId(id, courseId)
    }

    private fun checkTimeOverlap(
        existingTimes: List<ClassTime>,
        newTimes: List<ClassTime>,
    ): Boolean {
        for (new in newTimes) {
            for (existing in existingTimes) {
                if (new.dayOfWeek == existing.dayOfWeek) {
                    if (new.startMinute < existing.endMinute && new.endMinute > existing.startMinute) {
                        return true
                    }
                }
            }
        }
        return false
    }
}