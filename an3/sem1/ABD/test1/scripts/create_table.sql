CREATE DATABASE IF NOT EXISTS `name_basics`;

USE `name_basics`;

DROP TABLE IF EXISTS persons;

CREATE TABLE persons (
    nconst VARCHAR(255) PRIMARY KEY,
    primaryName VARCHAR(255) NOT NULL,
    birthYear VARCHAR(4),
    deathYear VARCHAR(4),
    primaryProfession VARCHAR(255),
    knownForTitles VARCHAR(255)
);