DROP TABLE IF EXISTS sim;

CREATE TABLE sim (
                     number BIGINT PRIMARY KEY,
                     status BOOLEAN NOT NULL,
                     minutes INT,
                     megabytes INT,
                     megabytesExpiration DATE,
                     minutesExpiration DATE,
);
