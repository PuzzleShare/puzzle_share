-- V5: Add gameName column to game_records table

ALTER TABLE game_records
    ADD COLUMN game_name VARCHAR(255) NULL COMMENT 'Name of the game';
