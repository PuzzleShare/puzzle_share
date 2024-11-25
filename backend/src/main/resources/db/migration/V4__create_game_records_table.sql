-- V4: Create Game Records Table

CREATE TABLE game_records (
                              record_id BIGINT AUTO_INCREMENT NOT NULL COMMENT 'Game record ID',
                              user_id BIGINT NOT NULL COMMENT 'User ID (foreign key)',
                              game_type VARCHAR(50) NOT NULL COMMENT 'Type of game (BATTLE, COOPERATION)',
                              players TEXT NULL COMMENT 'JSON-encoded list of all players',
                              team_mates TEXT NULL COMMENT 'JSON-encoded list of teammates',
                              opponents TEXT NULL COMMENT 'JSON-encoded list of opponents',
                              my_team VARCHAR(20) NULL COMMENT 'User’s team (RED, BLUE, or null)',
                              game_status VARCHAR(20) NULL COMMENT 'Game status (WIN, LOSS, DRAW, COMPLETED, FAILED)',
                              puzzle_image VARCHAR(512) NOT NULL COMMENT 'Puzzle image URL',
                              total_piece_count INT NOT NULL COMMENT 'Total number of puzzle pieces',
                              duration_in_minutes INT NOT NULL COMMENT 'Game duration in minutes',
                              played_at DATETIME NOT NULL COMMENT 'Game end timestamp',
                              PRIMARY KEY (record_id),
                              CONSTRAINT fk_game_records_users FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Stores game records for users';
