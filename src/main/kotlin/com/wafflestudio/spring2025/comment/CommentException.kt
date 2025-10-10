package com.wafflestudio.spring2025.comment

import com.wafflestudio.spring2025.DomainException
import org.springframework.http.HttpStatusCode

sealed class CommentException(
    errorCode: Int,
    httpStatusCode: HttpStatusCode,
    msg: String,
    cause: Throwable? = null,
) : DomainException(errorCode, httpStatusCode, msg, cause)

class CommentBlankContentException :
    CommentException(
        errorCode = 0,
        httpStatusCode = HttpStatusCode.valueOf(400),
        msg = "Content must not be blank",
    )

class CommentNotFoundException :
    CommentException(
        errorCode = 0,
        httpStatusCode = HttpStatusCode.valueOf(404),
        msg = "Comment not found",
    )

class CommentUpdateForbiddenException :
    CommentException(
        errorCode = 0,
        httpStatusCode = HttpStatusCode.valueOf(403),
        msg = "You don't have permission to update this comment",
    )
