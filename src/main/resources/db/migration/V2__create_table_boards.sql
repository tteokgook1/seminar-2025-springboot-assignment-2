CREATE TABLE IF NOT EXISTS boards
(
    id         BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6)  NOT NULL,
    updated_at TIMESTAMP(6)  NOT NULL
);
