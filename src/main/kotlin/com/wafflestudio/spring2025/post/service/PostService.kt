package com.wafflestudio.spring2025.post.service

import com.wafflestudio.spring2025.board.BoardNotFoundException
import com.wafflestudio.spring2025.board.repository.BoardRepository
import com.wafflestudio.spring2025.comment.repository.CommentRepository
import com.wafflestudio.spring2025.post.PostBlankContentException
import com.wafflestudio.spring2025.post.PostBlankTitleException
import com.wafflestudio.spring2025.post.PostNotFoundException
import com.wafflestudio.spring2025.post.PostUpdateForbiddenException
import com.wafflestudio.spring2025.post.dto.PostPaging
import com.wafflestudio.spring2025.post.dto.PostPagingResponse
import com.wafflestudio.spring2025.post.dto.core.PostDto
import com.wafflestudio.spring2025.post.model.Post
import com.wafflestudio.spring2025.post.repository.PostRepository
import com.wafflestudio.spring2025.user.model.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class PostService(
    private val postRepository: PostRepository,
    private val boardRepository: BoardRepository,
    private val commentRepository: CommentRepository,
) {
    fun get(postId: Long): PostDto {
        val postWithUserAndBoard =
            postRepository.findByIdWithUserAndBoard(postId)
                ?: throw PostNotFoundException()

        return PostDto(postWithUserAndBoard)
    }

    fun pageByBoardId(
        boardId: Long,
        nextCreatedAt: Instant?,
        nextId: Long?,
        limit: Int,
    ): PostPagingResponse {
        val board = boardRepository.findByIdOrNull(boardId) ?: throw BoardNotFoundException()

        val queryLimit = limit + 1
        val postWithUserAndBoards =
            postRepository.findByBoardIdWithCursor(board.id!!, nextCreatedAt, nextId, queryLimit)
        val hasNext = postWithUserAndBoards.size > limit
        val pagePosts = if (hasNext) postWithUserAndBoards.subList(0, limit) else postWithUserAndBoards
        val newNextCreatedAt = if (hasNext) pagePosts.last().createdAt else null
        val newNextId = if (hasNext) pagePosts.last().id else null

        return PostPagingResponse(
            pagePosts.map { PostDto(it) },
            PostPaging(newNextCreatedAt?.toEpochMilli(), newNextId, hasNext),
        )
    }

    fun create(
        title: String,
        content: String,
        user: User,
        boardId: Long,
    ): PostDto {
        if (title.isBlank()) {
            throw PostBlankTitleException()
        }
        if (content.isBlank()) {
            throw PostBlankContentException()
        }

        val board = boardRepository.findByIdOrNull(boardId) ?: throw BoardNotFoundException()

        val post =
            postRepository.save(
                Post(
                    title = title,
                    content = content,
                    userId = user.id!!,
                    boardId = board.id!!,
                ),
            )
        return PostDto(post, user, board)
    }

    fun update(
        postId: Long,
        title: String?,
        content: String?,
        user: User,
    ): PostDto {
        if (title?.isBlank() == true) {
            throw PostBlankTitleException()
        }
        if (content?.isBlank() == true) {
            throw PostBlankContentException()
        }

        val post =
            postRepository.findByIdOrNull(postId)
                ?: throw PostNotFoundException()

        if (post.userId != user.id) {
            throw PostUpdateForbiddenException()
        }

        title?.let { post.title = it }
        content?.let { post.content = it }
        postRepository.save(post)

        val postWithUserAndBoard = postRepository.findByIdWithUserAndBoard(post.id!!) ?: throw PostNotFoundException()
        return PostDto(postWithUserAndBoard)
    }

    fun delete(
        postId: Long,
        user: User,
    ) {
        val post =
            postRepository.findByIdOrNull(postId)
                ?: throw PostNotFoundException()

        if (post.userId != user.id) {
            throw PostUpdateForbiddenException()
        }

        commentRepository.deleteByPostId(postId)
        postRepository.delete(post)
    }
}
