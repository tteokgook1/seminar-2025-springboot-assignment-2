package com.wafflestudio.spring2025.post

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

sealed class PostException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class PostNotFoundException :
    PostException(
        errorCode = 0,
        httpStatusCode = HttpStatus.NOT_FOUND,
        msg = "Post not found",
    )

class PostBlankTitleException :
    PostException(
        errorCode = 0,
        httpStatusCode = HttpStatus.BAD_REQUEST,
        msg = "Title must not be blank",
    )

class PostBlankContentException :
    PostException(
        errorCode = 0,
        httpStatusCode = HttpStatus.BAD_REQUEST,
        msg = "Content must not be blank",
    )

class PostUpdateForbiddenException :
    PostException(
        errorCode = 0,
        httpStatusCode = HttpStatus.FORBIDDEN,
        msg = "You don't have permission to update this post",
    )
