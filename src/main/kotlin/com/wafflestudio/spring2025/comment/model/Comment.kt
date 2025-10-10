package com.wafflestudio.spring2025.comment.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("comments")
class Comment(
    @Id var id: Long? = null,
    var content: String,
    var userId: Long,
    var postId: Long,
    @CreatedDate
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
)
