CREATE EXTENSION IF NOT EXISTS postgis;

-- PROFILES
CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    age INT CHECK (age >= 18 AND age <= 99),
    description TEXT,
    location GEOGRAPHY(Point, 4326),

    email VARCHAR(255) UNIQUE NOT NULL,
    registered_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_login TIMESTAMP,
    notifications_enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE photos (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    image BYTEA NOT NULL, -- само изображение
    content_type VARCHAR(100) NOT NULL, -- например, image/png
    file_name VARCHAR(255), -- оригинальное имя файла
    position INT CHECK (position BETWEEN 1 AND 5),
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- INTERESTS
CREATE TABLE interests (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- PROFILE_INTERESTS
CREATE TABLE profile_interests (
    profile_id BIGINT NOT NULL REFERENCES profiles(id) ON DELETE CASCADE,
    interest_id BIGINT NOT NULL REFERENCES interests(id),
    PRIMARY KEY (profile_id, interest_id)
);

-- FACTS
CREATE TABLE facts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL -- e.g., "height", "religion", "has_children", "goal"
);


-- PROFILE_FACTS
CREATE TABLE profile_facts (
    profile_id BIGINT NOT NULL REFERENCES profiles(id) ON DELETE CASCADE, -- 2
    fact_id BIGINT NOT NULL REFERENCES facts(id), -- religion
    fact_value VARCHAR(100), -- orthodoxy
    PRIMARY KEY (profile_id, fact_id)
);
