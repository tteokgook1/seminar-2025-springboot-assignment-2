package com.wafflestudio.spring2025.comment.repository

import com.wafflestudio.spring2025.comment.model.Comment
import com.wafflestudio.spring2025.comment.model.CommentWithUser
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface CommentRepository : CrudRepository<Comment, Long> {
    fun findByIdAndPostId(
        id: Long,
        postId: Long,
    ): Comment?

    @Query(
        """
        SELECT
            c.id AS id,
            c.post_id AS post_id,
            c.content AS content,
            c.created_at AS created_at,
            c.updated_at AS updated_at,
            u.id AS user_id,
            u.username AS user_username
        FROM comments c
        JOIN users u ON u.id = c.user_id
        WHERE c.post_id = :postId
        ORDER BY c.created_at DESC
    """,
    )
    fun findAllWithUserByPostIdOrderByCreatedAtDesc(
        @Param("postId") postId: Long,
    ): List<CommentWithUser>

    fun deleteByPostId(postId: Long)
}
