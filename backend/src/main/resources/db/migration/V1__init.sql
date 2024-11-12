DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
    `user_id` BIGINT AUTO_INCREMENT NOT NULL,
    `user_name` VARCHAR(50) NOT NULL,
    `email` VARCHAR(254) NOT NULL,
    `social_type` VARCHAR(50) NOT NULL,
    `created_date` DATETIME NOT NULL,
    `modified_date` DATETIME NULL,
    PRIMARY KEY (`user_id`)
);
