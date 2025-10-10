package com.wafflestudio.spring2025.comment.dto.core

import com.wafflestudio.spring2025.comment.model.Comment
import com.wafflestudio.spring2025.comment.model.CommentWithUser
import com.wafflestudio.spring2025.user.dto.core.UserDto
import com.wafflestudio.spring2025.user.model.User

data class CommentDto(
    val id: Long?,
    val content: String,
    val postId: Long,
    val user: UserDto,
    val createdAt: Long,
    val updatedAt: Long,
) {
    constructor(comment: Comment, user: User) : this(
        id = comment.id,
        content = comment.content,
        postId = comment.postId,
        user = UserDto(user),
        createdAt = comment.createdAt!!.toEpochMilli(),
        updatedAt = comment.updatedAt!!.toEpochMilli(),
    )

    constructor(commentWithUser: CommentWithUser) : this(
        id = commentWithUser.id,
        content = commentWithUser.content,
        postId = commentWithUser.postId,
        user =
            UserDto(
                id = commentWithUser.user!!.id,
                username = commentWithUser.user.username,
            ),
        createdAt = commentWithUser.createdAt.toEpochMilli(),
        updatedAt = commentWithUser.updatedAt.toEpochMilli(),
    )
}
