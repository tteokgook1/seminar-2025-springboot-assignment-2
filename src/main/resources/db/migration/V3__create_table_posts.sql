CREATE TABLE IF NOT EXISTS posts
(
    id         BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    board_id   BIGINT       NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    created_at TIMESTAMP(6)  NOT NULL,
    updated_at TIMESTAMP(6)  NOT NULL,
    CONSTRAINT posts__fk__user_id
        FOREIGN KEY (user_id) REFERENCES users (id)
);
