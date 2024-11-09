DROP SCHEMA IF EXISTS PewPewSmash CASCADE;
CREATE SCHEMA PewPewSmash;
SET search_path TO PewPewSmash;

CREATE TABLE players (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE game_modes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ranks (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    image_url VARCHAR(255),  -- Image URL for rank badges or icons
    min_xp INTEGER NOT NULL,
    max_xp INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE player_ranks (
    id SERIAL PRIMARY KEY,
    player_id INTEGER NOT NULL REFERENCES players(id),
    rank_id INTEGER NOT NULL REFERENCES ranks(id),
    current_xp INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE leaderboards (
    id SERIAL PRIMARY KEY,
    player_id INTEGER NOT NULL REFERENCES players(id),
    rank INTEGER NOT NULL,
    score INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE achievements (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE player_achievements (
    id SERIAL PRIMARY KEY,
    player_id INTEGER NOT NULL REFERENCES players(id),
    achievement_id INTEGER NOT NULL REFERENCES achievements(id),
    unlocked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_players_email ON players(email);
CREATE INDEX idx_player_ranks_player_id ON player_ranks(player_id);
CREATE INDEX idx_leaderboards_player_id ON leaderboards(player_id);
CREATE INDEX idx_player_achievements_player_id ON player_achievements(player_id);

DROP PROCEDURE IF EXISTS update_timestamp;

CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_player_timestamp
BEFORE UPDATE ON players
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_rank_timestamp
BEFORE UPDATE ON ranks
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_achievement_timestamp
BEFORE UPDATE ON achievements
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

INSERT INTO players (username, email, password) 
VALUES
('T1WiLLi', 't1willi@gmail.com', '5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8'), -- passowrd
('xXProGamerXx', 'proGamer@gmail.com', '07ed400759a0f606a8b5bfa84712aabe7d1b1c45cb6536c8a5727446b6647b84'), -- pro
('l', 'l@gmail.com', 'acac86c0e609ca906f632b0e2dacccb2b77d22b0621f20ebece1a4835b93f6f0'); -- l
('xX_dark_gamer_tv_Xx63', 'dark@gmail.com', '15e2b0d3c33891ebb0f1ef609ec419420c20e320ce94c65fbc8c3312448eb225') -- 123456789

INSERT INTO game_modes (name, description, image_url)
VALUES
('Battle Royale', 'Can you survive the storm and be the last one standing?', 'battle_royale_image.png'),
('Arena', 'Kill as many players as you can in a set amount of time', 'arena_image.png'),
('Tutorial', 'Learn the basics of the game in a safe environment', 'tutorial_image.png');

INSERT INTO ranks (name, description, image_url, min_xp, max_xp)
VALUES
('Bronze', 'Starting rank for beginners', 'bronze_badge', 0, 1000),
('Silver', 'Intermediate rank for experienced players', 'silver_badge', 1001, 2000),
('Gold', 'Advanced rank for skilled players', 'gold_badge', 2001, 3000),
('Platinum', 'Top-tier rank for elite players', 'platinum_badge', 3001, 4000),
('Diamond', 'The cream of the cream, the best of the best', 'diamond_badge', 4001, 5000);


INSERT INTO player_ranks (player_id, rank_id, current_xp) 
VALUES
(1, 4, 3500), -- T1WiLLi with  3500 XP is at Platinum rank
(2, 1, 250), --  xXProGamerXx with 250 XP is at Bronze rank
(3, 5, 4325); -- l with 4325 XP is at Diamond rank


INSERT INTO achievements (name, description)
VALUES
('First Kill', 'Achieve your first kill in a match'),
('First Win', 'Win your first match'),
('First Death', 'Die in a match'),
('First Rank Up', 'Reach a new rank'),
('First Game Played', 'Play your first game'),
('Untouchable', 'Win a match without being damaged!'),
('Killer', 'Get 5 kills in a row'),
('Headshot Master', 'Achieve 5 headshots in one match'),
('Sharpshooter', 'Achieve 10 headshots in one match'),
('Survivor', 'Survive for 10 minutes in a match without dying'),
('Rampage', 'Get 10 kills in a row without dying'),
('Demolition Expert', 'Deal 500 damage in a single match'),
('Grenade Master', 'Get 3 kills with grenades in a single match'),
('Sniper Pro', 'Get 5 kills with a sniper rifle in a single match'),
('Assault Specialist', 'Get 10 kills with an assault rifle in a single match'),
('Shotgun Frenzy', 'Get 5 kills with a shotgun in close range'),
('Marathon Runner', 'Travel 1000 blocks in a single match'),
('Unstoppable', 'Win 3 consecutive matches'),
('Comeback King', 'Win a match after being behind in score'),
('Team Player', 'Win 5 matches in team-based game modes');








