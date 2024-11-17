CREATE TABLE IF NOT EXISTS users (
    id            BIGSERIAL PRIMARY KEY,
    username      TEXT NOT NULL UNIQUE,
    email         TEXT NOT NULL UNIQUE,
    password      TEXT NOT NULL,
    role          TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tasks (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT ,
    status TEXT,
    priority TEXT,
    author_id BIGINT,
    assignee_id BIGINT
);

CREATE TABLE IF NOT EXISTS comments(
    id BIGSERIAL PRIMARY KEY ,
    user_id BIGINT NOT NULL ,
    task_id BIGINT NOT NULL ,
    text TEXT NOT NULL ,
    CONSTRAINT fk_task FOREIGN KEY (task_id ) REFERENCES tasks (id) ON DELETE CASCADE

);