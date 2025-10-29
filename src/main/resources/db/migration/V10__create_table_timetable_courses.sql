CREATE TABLE IF NOT EXISTS `timetable_courses` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `timetable_id` BIGINT NOT NULL COMMENT 'Timetable ID',
    `course_id` BIGINT NOT NULL COMMENT 'Course ID',
    PRIMARY KEY (`id`),
    CONSTRAINT `timetable_courses__fk__timetable_id` FOREIGN KEY (`timetable_id`) REFERENCES `timetables` (`id`) ON DELETE CASCADE,
    CONSTRAINT `timetable_courses__fk__course_id` FOREIGN KEY (`course_id`) REFERENCES `courses` (`id`) ON DELETE CASCADE,
    UNIQUE KEY `uk_timetable_course` (`timetable_id`, `course_id`)
);
