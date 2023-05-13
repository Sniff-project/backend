CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    avatar VARCHAR(255),
    firstname VARCHAR(30) NOT NULL,
    lastname VARCHAR(30) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL,
    region VARCHAR(50),
    city VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS pet (
    id SERIAL PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    photos TEXT,
    name VARCHAR(30) NOT NULL,
    latitude VARCHAR(50) NOT NULL,
    longitude VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    found_or_lost_date DATE NOT NULL,
    description VARCHAR(500),
    author_id SERIAL,
    CONSTRAINT fk_pet_author
        FOREIGN KEY (author_id)
            REFERENCES users(id)
);

ALTER TABLE users
    ADD COLUMN pet_id SERIAL,
    ADD CONSTRAINT fk_users_pet
        FOREIGN KEY (pet_id)
            REFERENCES pet(id);

ALTER TABLE pet
    ADD CONSTRAINT chk_pet_gender
        CHECK (gender IN ('MALE', 'FEMALE', 'UNKNOWN')),
    ADD CONSTRAINT chk_pet_status
        CHECK (status IN ('LOST', 'FOUND'));