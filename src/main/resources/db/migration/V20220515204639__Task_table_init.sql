CREATE TABLE IF NOT EXISTS task
(
    id         BIGSERIAL PRIMARY KEY,
    title      varchar(100) NOT NULL,
    priority   varchar(50)  NOT NULL,
    status     varchar(50)  NOT NULL,
    created_at timestamp    NOT NULL,
    updated_at timestamp    NOT NULL,
    user_id    bigint       NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);