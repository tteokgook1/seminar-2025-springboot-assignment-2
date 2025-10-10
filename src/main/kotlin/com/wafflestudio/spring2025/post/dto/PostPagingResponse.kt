package com.wafflestudio.spring2025.post.dto

import com.wafflestudio.spring2025.post.dto.core.PostDto

data class PostPagingResponse(
    val data: List<PostDto>,
    val paging: PostPaging,
)
