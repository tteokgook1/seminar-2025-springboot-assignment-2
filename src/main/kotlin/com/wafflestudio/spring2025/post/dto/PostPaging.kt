package com.wafflestudio.spring2025.post.dto

data class PostPaging(
    val nextCreatedAt: Long?,
    val nextId: Long?,
    val hasNext: Boolean,
)
