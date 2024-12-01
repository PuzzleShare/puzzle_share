-- V6: Add myPercent column to game_records table

ALTER TABLE game_records
    ADD COLUMN my_percent FLOAT NULL COMMENT 'my percent of the puzzle';
