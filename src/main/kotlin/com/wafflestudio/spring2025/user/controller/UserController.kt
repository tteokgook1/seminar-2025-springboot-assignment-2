package com.wafflestudio.spring2025.user.controller

import com.wafflestudio.spring2025.user.LoggedInUser
import com.wafflestudio.spring2025.user.dto.GetMeResponse
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController {
    @GetMapping("/me")
    fun me(
        @LoggedInUser user: User,
    ): ResponseEntity<GetMeResponse> = ResponseEntity.ok(UserDto(user))
}
