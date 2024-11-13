CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT NOT NULL,
    user_name VARCHAR(50) NOT NULL,
    email VARCHAR(254),
    social_type VARCHAR(50) NOT NULL,
    user_image VARCHAR(512) NOT NULL,
    created_date DATETIME NOT NULL,
    modified_date DATETIME,
    PRIMARY KEY (user_id)
);