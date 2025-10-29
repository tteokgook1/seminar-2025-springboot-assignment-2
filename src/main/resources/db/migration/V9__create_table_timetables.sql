CREATE TABLE IF NOT EXISTS `timetables` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT 'User ID',
    `name` VARCHAR(255) NOT NULL COMMENT '시간표 이름',
    `year` INT NOT NULL COMMENT '대상 연도',
    `semester` INT NOT NULL COMMENT '대상 학기',
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `timetables__fk__user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_timetables` (`user_id`, `year`, `semester`, `name`)
);
