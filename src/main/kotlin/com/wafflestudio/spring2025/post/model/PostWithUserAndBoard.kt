package com.wafflestudio.spring2025.post.model

import org.springframework.data.relational.core.mapping.Embedded
import java.time.Instant

data class PostWithUserAndBoard(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Embedded.Nullable(prefix = "user_")
    val user: User?,
    @Embedded.Nullable(prefix = "board_")
    val board: Board?,
) {
    data class User(
        val id: Long,
        val username: String,
    )

    data class Board(
        val id: Long,
        val name: String,
    )
}
