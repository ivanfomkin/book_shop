DROP TABLE IF EXISTS books;
CREATE TABLE books
(
    id        BIGSERIAL PRIMARY KEY,
    author_id VARCHAR(250) NOT NULL,
    title     VARCHAR(250) NOT NULL,
    price_old VARCHAR(250) DEFAULT NULL,
    price     VARCHAR(250) DEFAULT NULL
);

DROP TABLE IF EXISTS authors;
CREATE TABLE authors
(
    id         SERIAL PRIMARY KEY,
    first_name varchar(250) NOT NULL,
    last_name  varchar(250) NOT NULL
);