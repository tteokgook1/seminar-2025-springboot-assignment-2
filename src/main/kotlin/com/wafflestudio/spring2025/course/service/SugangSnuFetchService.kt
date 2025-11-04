package com.wafflestudio.spring2025.course.service

import com.wafflestudio.spring2025.course.model.ClassTime
import com.wafflestudio.spring2025.course.model.Course
import com.wafflestudio.spring2025.course.repository.ClassTimeRepository
import com.wafflestudio.spring2025.course.repository.CourseRepository
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.io.ByteArrayInputStream

@Service
class SugangSnuFetchService(
    private val webClient: WebClient,
    private val courseRepository: CourseRepository,
    private val classTimeRepository: ClassTimeRepository,
    @Value("\${sugang.init-url}") private val initUrl: String,
    @Value("\${sugang.ajax-url}") private val ajaxUrl: String,
    @Value("\${sugang.search-url}") private val searchUrl: String,
    @Value("\${sugang.export-url}") private val exportUrl: String,
) {
    // 1: 봄, 2: 가을, 3: 여름, 4: 겨울
    private fun mapSemesterToSemCode(semester: Int): String =
        when (semester) {
            1 -> "U000200001U000300001" // 1학기 (봄)
            2 -> "U000200002U000300002" // 2학기 (가을)
            3 -> "U000200001U000300002" // 여름계절
            4 -> "U000200002U000300001" // 겨울계절
            else -> throw IllegalArgumentException("Invalid semester code: $semester")
        }

    private fun mapDayCharToWeekday(day: Char): Int =
        when (day) {
            '월' -> 1
            '화' -> 2
            '수' -> 3
            '목' -> 4
            '금' -> 5
            '토' -> 6
            '일' -> 7
            else -> 0
        }

    private fun fetchXlsBytes(
        year: Int,
        semester: Int,
    ): ByteArray {
        val semCode = mapSemesterToSemCode(semester)

        val refererUrl = initUrl

        webClient
            .get()
            .uri(initUrl)
            .retrieve()
            .toBodilessEntity()
            .block()

        webClient
            .post()
            .uri(ajaxUrl)
            .header(HttpHeaders.REFERER, "https://sugang.snu.ac.kr/sugang/co/co010.action") // <-- Referer 헤더 추가
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters
                    .fromFormData("openUpDeptCd", "")
                    .with("openDeptCd", "")
                    .with("srchOpenSchyy", year.toString())
                    .with("srchOpenShtm", semCode),
            ).retrieve()
            .toBodilessEntity()
            .block()

        val searchBody = baseSearchForm(year, semCode).toFormDataInserter()
        webClient
            .post()
            .uri(searchUrl)
            .header(HttpHeaders.REFERER, "https://sugang.snu.ac.kr/sugang/co/co010.action") // <-- Referer 헤더 추가
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(searchBody)
            .retrieve()
            .toBodilessEntity()
            .block()

        val exportBody = baseExportForm(year, semCode).toFormDataInserter()
        val responseEntity =
            webClient
                .post()
                .uri(exportUrl)
                .header(HttpHeaders.REFERER, "https://sugang.snu.ac.kr/sugang/co/co010.action")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(exportBody)
                .retrieve()
                .onStatus({ !it.is2xxSuccessful }) { resp ->
                    resp.bodyToMono(String::class.java).map { body ->
                        IllegalStateException("Excel export failed: ${resp.statusCode()} body=$body")
                    }
                }.toEntity(ByteArray::class.java)
                .block()!!

        val contentType = responseEntity.headers.contentType?.toString() ?: ""
        require(contentType.contains("application/vnd.ms-excel") || contentType.contains("application/octet-stream")) {
            "Unexpected content type from sugang server: $contentType\n" +
                "Body preview: ${String(responseEntity.body ?: ByteArray(0)).take(200)}"
        }

        return responseEntity.body ?: throw IllegalStateException("Excel export returned empty body")
    }

    @Transactional
    fun fetchAndSaveCourses(
        year: Int,
        semester: Int,
    ) {
        val bytes = fetchXlsBytes(year, semester)
        HSSFWorkbook(ByteArrayInputStream(bytes)).use { workbook ->

            val sheet: Sheet = workbook.getSheetAt(0)

            val headerRow =
                sheet.getRow(2)
                    ?: throw IllegalStateException("XLS Header row not found. Check if row index 2 is correct.")

            val headerMap = headerRow.associate { it.toString().trim() to it.columnIndex }

            val getCell: (Row, String) -> String = { row, key ->
                val idx = headerMap.entries.firstOrNull { it.key.contains(key) }?.value
                row.getCell(idx ?: -1)?.toString()?.trim() ?: ""
            }

            for (i in 3..sheet.lastRowNum) {
                val row = sheet.getRow(i) ?: continue

                val courseNumber = getCell(row, "교과목번호")
                val lectureNumber = getCell(row, "강좌번호")

                // 교과목번호나 강좌번호 없으면 건너뛰기
                if (courseNumber.isBlank() || lectureNumber.isBlank()) continue

                val course =
                    Course(
                        year = year,
                        semester = semester,
                        classification = getCell(row, "교과구분").ifBlank { null },
                        college = getCell(row, "개설대학").ifBlank { null },
                        department = getCell(row, "개설학과").ifBlank { null },
                        program = getCell(row, "이수과정").ifBlank { null },
                        grade = getCell(row, "학년").toIntOrNull(),
                        courseNumber = courseNumber,
                        lectureNumber = lectureNumber,
                        courseTitle = getCell(row, "교과목명"),
                        credits = getCell(row, "학점").toIntOrNull() ?: 0,
                        professor = getCell(row, "주담당교수").ifBlank { null },
                    )
                val savedCourse = courseRepository.save(course)

                val timeString = getCell(row, "수업교시")
                val roomString = getCell(row, "강의실")
                parseAndSaveClassTimes(timeString, roomString, savedCourse.id!!)
            }
        }
    }

    private fun parseAndSaveClassTimes(
        timeStringRaw: String,
        roomStringRaw: String,
        courseId: Long,
    ) {
        if (timeStringRaw.isBlank()) return

        val roomString = roomStringRaw.replace("\n", "/").replace("(무선랜제공)", "").trim()
        val timeString = timeStringRaw.replace("\n", "/").replace("\\s+".toRegex(), "")

        val timeParts = timeString.split("/").filter { it.isNotBlank() }
        val roomParts = if (roomString.isBlank()) emptyList() else roomString.split("/")

        val getLocation = { i: Int ->
            roomParts.getOrNull(i) ?: if (roomParts.size == 1) roomParts[0] else ""
        }

        val regex = Regex("""^([월화수목금토일])\((\d{1,2}):(\d{2})~(\d{1,2}):(\d{2})\)$""")

        for ((i, part) in timeParts.withIndex()) {
            val match = regex.matchEntire(part) ?: continue

            val dayChar = match.groupValues[1].first()
            val startH = match.groupValues[2].toInt()
            val startM = match.groupValues[3].toInt()
            val endH = match.groupValues[4].toInt()
            val endM = match.groupValues[5].toInt()

            val location = getLocation(i).ifBlank { null }

            classTimeRepository.save(
                ClassTime(
                    courseId = courseId,
                    dayOfWeek = mapDayCharToWeekday(dayChar),
                    startMinute = startH * 60 + startM,
                    endMinute = endH * 60 + endM,
                    location = location,
                ),
            )
        }
    }

    private fun baseSearchForm(
        year: Int,
        semCode: String,
    ): Map<String, String> =
        mapOf(
            "workType" to "S",
            "pageNo" to "1",
            "srchOpenSchyy" to year.toString(),
            "srchOpenShtm" to semCode,
            "srchSbjtNm" to "",
            "srchSbjtCd" to "",
            "seeMore" to "닫기",
            "srchCptnCorsFg" to "",
            "srchOpenShyr" to "",
            "srchOpenUpSbjtFldCd" to "",
            "srchOpenSbjtFldCd" to "",
            "srchOpenUpDeptCd" to "",
            "srchOpenDeptCd" to "",
            "srchOpenMjCd" to "",
            "srchOpenSubmattCorsFg" to "",
            "srchOpenSubmattFgCd1" to "",
            "srchOpenSubmattFgCd2" to "",
            "srchOpenSubmattFgCd3" to "",
            "srchOpenSubmattFgCd4" to "",
            "srchOpenSubmattFgCd5" to "",
            "srchOpenSubmattFgCd6" to "",
            "srchOpenSubmattFgCd7" to "",
            "srchOpenSubmattFgCd8" to "",
            "srchOpenSubmattFgCd9" to "",
            "srchExcept" to "",
            "srchOpenPntMin" to "",
            "srchOpenPntMax" to "",
            "srchCamp" to "",
            "srchBdNo" to "",
            "srchProfNm" to "",
            "srchOpenSbjtTmNm" to "",
            "srchOpenSbjtDayNm" to "",
            "srchOpenSbjtTm" to "",
            "srchOpenSbjtNm" to "",
            "srchTlsnAplyCapaCntMin" to "",
            "srchTlsnAplyCapaCntMax" to "",
            "srchLsnProgType" to "",
            "srchTlsnRcntMin" to "",
            "srchTlsnRcntMax" to "",
            "srchMrksGvMthd" to "",
            "srchIsEngSbjt" to "",
            "srchMrksApprMthdChgPosbYn" to "",
            "srchIsPendingCourse" to "",
            "srchGenrlRemoteLtYn" to "",
            "srchLanguage" to "ko",
            "srchCurrPage" to "1",
            "srchPageSize" to "9999",
        )

    private fun baseExportForm(
        year: Int,
        semCode: String,
    ): Map<String, String> =
        baseSearchForm(year, semCode).toMutableMap().apply {
            this["workType"] = "EX"
        }

    private fun Map<String, String>.toFormDataInserter(): BodyInserters.FormInserter<String> {
        var inserter = BodyInserters.fromFormData(firstKeyOrEmpty(), firstValueOrEmpty())
        this.entries.drop(1).forEach { (k, v) -> inserter = inserter.with(k, v) }
        return inserter
    }

    private fun Map<String, String>.firstKeyOrEmpty() = keys.firstOrNull() ?: ""

    private fun Map<String, String>.firstValueOrEmpty() = values.firstOrNull() ?: ""
}
