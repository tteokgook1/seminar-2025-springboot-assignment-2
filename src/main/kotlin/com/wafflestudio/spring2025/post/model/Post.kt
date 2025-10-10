package com.wafflestudio.spring2025.post.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.Instant

@Table("posts")
class Post(
    @Id var id: Long? = null,
    var title: String,
    var content: String,
    var userId: Long,
    var boardId: Long,
    @CreatedDate
    var createdAt: Instant? = null,
    @LastModifiedDate
    var updatedAt: Instant? = null,
)
