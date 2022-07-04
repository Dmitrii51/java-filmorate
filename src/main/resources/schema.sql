CREATE TABLE IF NOT EXISTS genre (
    genre_id integer PRIMARY KEY,
    title varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa_rating (
    mpa_rating_id integer PRIMARY KEY,
    name varchar NOT NULL,
    description varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar,
    description varchar,
    duration integer,
    release_date date,
    mpa_rating_id integer REFERENCES mpa_rating (mpa_rating_id),
    rate int
);

CREATE TABLE IF NOT EXISTS film_genres (
    id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id integer REFERENCES films (film_id),
    genre_id integer REFERENCES genre (genre_id)
);

CREATE TABLE IF NOT EXISTS users (
    user_id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    login varchar,
    name varchar,
    birthday date,
    email varchar
);

CREATE TABLE IF NOT EXISTS likes (
    id integer GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id integer REFERENCES films (film_id),
    user_id integer REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS friendship_status (
    friendship_status_id integer PRIMARY KEY,
    title varchar NOT NULL,
    description varchar NOT NULL
);

CREATE TABLE IF NOT EXISTS friendship (
    user_id integer REFERENCES users (user_id),
    friend_id integer REFERENCES users (user_id),
    PRIMARY KEY (user_id, friend_id),
    friendship_status_id integer REFERENCES friendship_status (friendship_status_id)
);
