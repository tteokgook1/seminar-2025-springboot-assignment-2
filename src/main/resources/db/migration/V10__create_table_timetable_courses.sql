CREATE TABLE `timetable_courses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `timetable_id` BIGINT NOT NULL COMMENT 'Timetable ID',
    `course_id` BIGINT NOT NULL COMMENT 'Course ID',
    PRIMARY KEY (`id`),
    FOREIGN KEY (`timetable_id`) REFERENCES `timetables` (`id`) ON DELETE CASCADE,
    FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_timetable_course` (`timetable_id`, `course_id`)
);
