CREATE TABLE IF NOT EXISTS comments
(
    id         BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    post_id    BIGINT       NOT NULL,
    user_id    BIGINT       NOT NULL,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP(6)  NOT NULL,
    updated_at TIMESTAMP(6)  NOT NULL,
    CONSTRAINT comments__fk__post_id
        FOREIGN KEY (post_id) REFERENCES posts (id),
    CONSTRAINT comments__fk__user_id
        FOREIGN KEY (user_id) REFERENCES users (id)
);
