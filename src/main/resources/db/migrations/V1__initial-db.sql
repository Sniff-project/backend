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
