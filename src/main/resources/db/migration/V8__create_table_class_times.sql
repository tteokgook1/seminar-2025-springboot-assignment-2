CREATE TABLE IF NOT EXISTS `class_times` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `course_id` BIGINT NOT NULL COMMENT 'Course ID',
    `day_of_week` INT NOT NULL COMMENT '요일 (0:월, 1:화, 2:수, 3:목, 4:금, 5:토, 6:일)',
    `start_minute` INT NOT NULL COMMENT '시작 시간 (분 단위, 예: 10:00 -> 600)',
    `end_minute` INT NOT NULL COMMENT '종료 시간 (분 단위, 예: 11:50 -> 710)',
    `location` VARCHAR(255) COMMENT '강의실',
    PRIMARY KEY (`id`),
    CONSTRAINT `class_times__fk__course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE
);
