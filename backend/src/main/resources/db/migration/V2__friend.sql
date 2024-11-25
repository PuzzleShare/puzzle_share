-- friends 테이블 생성
DROP TABLE IF EXISTS `friends`;
CREATE TABLE IF NOT EXISTS `friends` (
                                         `id` BIGINT AUTO_INCREMENT NOT NULL,
                                         `requester_id` BIGINT NOT NULL,
                                         `receiver_id` BIGINT NOT NULL,
                                         `status` ENUM('PENDING', 'ACCEPTED') DEFAULT 'PENDING' NOT NULL,
    `created_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `modified_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`requester_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE,
    FOREIGN KEY (`receiver_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE
    );
