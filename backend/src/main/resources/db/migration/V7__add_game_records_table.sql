ALTER TABLE game_records
    ADD COLUMN game_id VARCHAR(255) COMMENT 'gameId of the puzzle for user';
ALTER TABLE game_records
    ADD COLUMN battle_timer INT NOT NULL COMMENT 'battletimer of the puzzle game';
