CREATE TABLE IF NOT EXISTS frankup_players (
    uuid       CHAR(36) CHARACTER SET ascii NOT NULL,
    rank_index INT NOT NULL,
    PRIMARY KEY (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
