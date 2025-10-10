package com.wafflestudio.spring2025.comment.model

import org.springframework.data.relational.core.mapping.Embedded
import java.time.Instant

data class CommentWithUser(
    val id: Long,
    val postId: Long,
    val content: String,
    val createdAt: Instant,
    val updatedAt: Instant,
    @Embedded.Nullable(prefix = "user_")
    val user: User?,
) {
    data class User(
        val id: Long,
        val username: String,
    )
}
