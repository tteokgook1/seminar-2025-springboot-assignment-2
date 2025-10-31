package com.wafflestudio.spring2025

import com.fasterxml.jackson.databind.ObjectMapper
import com.wafflestudio.spring2025.helper.DataGenerator
import com.wafflestudio.spring2025.timetable.dto.TimetablesResponse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
class TimetableIntegrationTest
    @Autowired
    constructor(
        private val mvc: MockMvc,
        private val mapper: ObjectMapper,
        private val dataGenerator: DataGenerator,
    ) {
        @Test
        fun `should create a timetable`() {
            // 시간표를 생성할 수 있다
            val (user, token) = dataGenerator.generateUser()

            val request =
                mapOf(
                    "name" to "새 시간표 제목",
                    "year" to 2025,
                    "semester" to 1,
                )

            mvc
                .perform(
                    post("/api/v1/timetables")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)),
                ).andExpect(status().isCreated)
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.user.id").value(user.id!!))
                .andExpect(jsonPath("$.name").value(request["name"]))
                .andExpect(jsonPath("$.year").value(request["year"]))
                .andExpect(jsonPath("$.semester").value(request["semester"]))
        }

        @Test
        fun `should not create a timetable with invalid request body`() {
            // 유효하지 않은 값으로 시간표를 생성할 수 없다
            val (_, token) = dataGenerator.generateUser()

            mvc
                .perform(
                    post("/api/v1/timetables")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            mapper.writeValueAsString(
                                mapOf(
                                    "name" to "   ",
                                    "year" to 2025,
                                    "semester" to 1,
                                ),
                            ),
                        ),
                ).andExpect(status().isBadRequest)

            mvc
                .perform(
                    post("/api/v1/timetables")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            mapper.writeValueAsString(
                                mapOf(
                                    "name" to "새 시간표 이름",
                                    "semester" to 1,
                                ),
                            ),
                        ),
                ).andExpect(status().isBadRequest)

            mvc
                .perform(
                    post("/api/v1/timetables")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            mapper.writeValueAsString(
                                mapOf(
                                    "name" to "새 시간표 이름",
                                    "year" to 2025,
                                    "semester" to 5,
                                ),
                            ),
                        ),
                ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not create a timetable with same user_id, name, year, semester`() {
            // 동일한 사용자, 이름, 연도, 학기로 시간표를 생성할 수 없다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)

            mvc
                .perform(
                    post("/api/v1/timetables")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            mapper.writeValueAsString(
                                mapOf(
                                    "name" to timetable.name,
                                    "year" to timetable.year,
                                    "semester" to timetable.semester,
                                ),
                            ),
                        ),
                ).andExpect(status().isConflict)
        }

        @Test
        fun `should retrieve all own timetables`() {
            // 자신의 모든 시간표 목록을 조회할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val cnt = 5
            repeat(cnt) { dataGenerator.generateTimetable(user = user) }

            val response =
                mvc
                    .perform(
                        get("/api/v1/timetables")
                            .header("Authorization", "Bearer $token"),
                    ).andExpect(status().isOk)
                    .andReturn()
                    .response
                    .getContentAsString(Charsets.UTF_8)
                    .let {
                        mapper.readValue(it, TimetablesResponse::class.java)
                    }

            assertTrue(
                response.data.size == cnt,
                "Failed to retrieve all own timetables. Expected: $cnt. Found: ${response.data.size}",
            )
        }

        @Test
        fun `should retrieve timetable details`() {
            // 시간표 상세 정보를 조회할 수 있다
        }

        @Test
        fun `should update timetable name`() {
            // 시간표 이름을 수정할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)

            mvc
                .perform(
                    patch("/api/v1/timetables/${timetable.id}")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            mapper.writeValueAsString(
                                mapOf(
                                    "name" to "새 시간표 이름",
                                ),
                            ),
                        ),
                ).andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(timetable.id))
                .andExpect(jsonPath("$.user.id").value(user.id!!))
                .andExpect(jsonPath("$.name").value("새 시간표 이름"))
                .andExpect(jsonPath("$.year").value(timetable.year))
                .andExpect(jsonPath("$.semester").value(timetable.semester))
        }

        @Test
        fun `should not update timetable with invalid name`() {
            // 공백 이름으로 시간표 이름을 수정할 수 없다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)

            mvc
                .perform(
                    patch("/api/v1/timetables/${timetable.id}")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            mapper.writeValueAsString(
                                mapOf(
                                    "name" to "    ",
                                ),
                            ),
                        ),
                ).andExpect(status().isBadRequest)
        }

        @Test
        fun `should not update another user's timetable`() {
            // 다른 사람의 시간표는 수정할 수 없다
            val (user, _) = dataGenerator.generateUser()
            val (_, token2) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)

            mvc
                .perform(
                    patch("/api/v1/timetables/${timetable.id}")
                        .header("Authorization", "Bearer $token2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            mapper.writeValueAsString(
                                mapOf(
                                    "name" to "새 시간표 이름",
                                ),
                            ),
                        ),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should not update not-found timetable`() {
            // 없는 시간표를 수정할 수 없다
            val (_, token) = dataGenerator.generateUser()

            mvc
                .perform(
                    patch("/api/v1/timetables/${Random.nextInt(1000000)}")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            mapper.writeValueAsString(
                                mapOf(
                                    "name" to "새 시간표 이름",
                                ),
                            ),
                        ),
                ).andExpect(status().isNotFound)
        }

        @Test
        fun `should not update timetable name to name of another existing timetable`() {
            // 다른 시간표와 동일한 이름으로 시간표를 수정할 수 없다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)
            val timetable2 = dataGenerator.generateTimetable(user = user, year = timetable.year, semester = timetable.semester)

            mvc
                .perform(
                    patch("/api/v1/timetables/${timetable.id}")
                        .header("Authorization", "Bearer $token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                            mapper.writeValueAsString(
                                mapOf(
                                    "name" to timetable2.name,
                                ),
                            ),
                        ),
                ).andExpect(status().isConflict)
        }

        @Test
        fun `should delete a timetable`() {
            // 시간표를 삭제할 수 있다
            val (user, token) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)

            mvc
                .perform(
                    delete("/api/v1/timetables/${timetable.id}")
                        .header("Authorization", "Bearer $token"),
                ).andExpect(status().isNoContent)

            mvc
                .perform(
                    get("/api/v1/timetables/${timetable.id}")
                        .header("Authorization", "Bearer $token"),
                ).andExpect(status().isNotFound)
        }

        @Test
        fun `should not delete another user's timetable`() {
            // 다른 사람의 시간표는 삭제할 수 없다
            val (user, _) = dataGenerator.generateUser()
            val (_, token2) = dataGenerator.generateUser()
            val timetable = dataGenerator.generateTimetable(user = user)

            mvc
                .perform(
                    delete("/api/v1/timetables/${timetable.id}")
                        .header("Authorization", "Bearer $token2"),
                ).andExpect(status().isForbidden)
        }

        @Test
        fun `should not delete not-found timetable`() {
            // 없는 시간표를 삭제할 수 없다
            val (_, token) = dataGenerator.generateUser()

            mvc
                .perform(
                    delete("/api/v1/timetables/${Random.nextInt(1000000)}")
                        .header("Authorization", "Bearer $token"),
                ).andExpect(status().isNotFound)
        }

        @Test
        fun `should search for courses`() {
            // 강의를 검색할 수 있다
        }

        @Test
        fun `should add a course to timetable`() {
            // 시간표에 강의를 추가할 수 있다
        }

        @Test
        fun `should return error when adding overlapping course to timetable`() {
            // 시간표에 강의 추가 시, 시간이 겹치면 에러를 반환한다
        }

        @Test
        fun `should not add a course to another user's timetable`() {
            // 다른 사람의 시간표에는 강의를 추가할 수 없다
        }

        @Test
        fun `should remove a course from timetable`() {
            // 시간표에서 강의를 삭제할 수 있다
        }

        @Test
        fun `should not remove a course from another user's timetable`() {
            // 다른 사람의 시간표에서는 강의를 삭제할 수 없다
        }

        @Test
        @Disabled("곧 안내드리겠습니다")
        fun `should fetch and save course information from SNU course registration site`() {
            // 서울대 수강신청 사이트에서 강의 정보를 가져와 저장할 수 있다
        }

        @Test
        fun `should return correct course list and total credits when retrieving timetable details`() {
            // 시간표 상세 조회 시, 강의 정보 목록과 총 학점이 올바르게 반환된다
        }

        @Test
        fun `should paginate correctly when searching for courses`() {
            // 강의 검색 시, 페이지네이션이 올바르게 동작한다
        }
    }
