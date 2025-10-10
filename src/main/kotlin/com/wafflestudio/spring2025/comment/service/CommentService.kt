package com.wafflestudio.spring2025.comment.service

import com.wafflestudio.spring2025.comment.CommentBlankContentException
import com.wafflestudio.spring2025.comment.CommentNotFoundException
import com.wafflestudio.spring2025.comment.CommentUpdateForbiddenException
import com.wafflestudio.spring2025.comment.dto.core.CommentDto
import com.wafflestudio.spring2025.comment.model.Comment
import com.wafflestudio.spring2025.comment.repository.CommentRepository
import com.wafflestudio.spring2025.post.PostNotFoundException
import com.wafflestudio.spring2025.post.repository.PostRepository
import com.wafflestudio.spring2025.user.model.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
) {
    fun list(postId: Long): List<CommentDto> {
        val post =
            postRepository.findByIdOrNull(postId)
                ?: throw PostNotFoundException()
        return commentRepository
            .findAllWithUserByPostIdOrderByCreatedAtDesc(post.id!!)
            .map { CommentDto(it) }
    }

    fun create(
        postId: Long,
        content: String,
        user: User,
    ): CommentDto {
        if (content.isBlank()) {
            throw CommentBlankContentException()
        }

        val post =
            postRepository.findByIdOrNull(postId)
                ?: throw PostNotFoundException()

        val comment =
            commentRepository.save(
                Comment(
                    postId = post.id!!,
                    content = content,
                    userId = user.id!!,
                ),
            )
        return CommentDto(comment, user)
    }

    fun update(
        commentId: Long,
        postId: Long,
        content: String?,
        user: User,
    ): CommentDto {
        if (content?.isBlank() == true) {
            throw CommentBlankContentException()
        }

        val comment =
            commentRepository.findByIdAndPostId(commentId, postId)
                ?: throw CommentNotFoundException()

        if (comment.userId != user.id) {
            throw CommentUpdateForbiddenException()
        }

        content?.let { comment.content = it }
        val updatedComment = commentRepository.save(comment)
        return CommentDto(updatedComment, user)
    }

    fun delete(
        commentId: Long,
        postId: Long,
        user: User,
    ) {
        val comment =
            commentRepository.findByIdAndPostId(commentId, postId)
                ?: throw CommentNotFoundException()

        if (comment.userId != user.id) {
            throw CommentUpdateForbiddenException()
        }

        commentRepository.delete(comment)
    }
}
