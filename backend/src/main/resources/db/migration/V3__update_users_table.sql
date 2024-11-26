-- V3: Update Users table to add new columns for game statistics and defaults

-- Add win_count column if not exists
ALTER TABLE `users` ADD COLUMN win_count INT DEFAULT 0 NOT NULL COMMENT 'Number of wins';

-- Add loss_count column if not exists
ALTER TABLE `users` ADD COLUMN loss_count INT DEFAULT 0 NOT NULL COMMENT 'Number of losses';

-- Add draw_count column if not exists
ALTER TABLE `users` ADD COLUMN draw_count INT DEFAULT 0 NOT NULL COMMENT 'Number of draws';

-- Add total_games column if not exists
ALTER TABLE `users` ADD COLUMN total_games INT DEFAULT 0 NOT NULL COMMENT 'Total number of games played';

-- Add win_rate column if not exists
ALTER TABLE `users` ADD COLUMN win_rate DOUBLE DEFAULT 0.0 NOT NULL COMMENT 'Win rate as a percentage';

-- Ensure all new columns have default values (optional)
-- Since the columns are already defined with defaults in the ADD COLUMN statement,
-- this section can be removed if no modifications are required.
ALTER TABLE `users`
    MODIFY win_count INT DEFAULT 0 NOT NULL,
    MODIFY loss_count INT DEFAULT 0 NOT NULL,
    MODIFY draw_count INT DEFAULT 0 NOT NULL,
    MODIFY total_games INT DEFAULT 0 NOT NULL,
    MODIFY win_rate DOUBLE DEFAULT 0.0 NOT NULL;
