CREATE TABLE IF NOT EXISTS users
(
    id                  BIGSERIAL PRIMARY KEY,
    encoded_password    varchar(100)        NOT NULL,
    email               varchar(320) UNIQUE NOT NULL,
    phone               varchar(16) UNIQUE,
    image_name          varchar(255) UNIQUE,
    role                varchar(20)         NOT NULL,
    status              varchar(20)         NOT NULL,
    last_logged_in_time timestamp,
    created_at          timestamp           NOT NULL,
    updated_at          timestamp           NOT NULL
);

CREATE TABLE IF NOT EXISTS forgot_password
(
    guid         varchar(36) PRIMARY KEY,
    request_time timestamp NOT NULL,
    user_id      bigint    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS user_verification
(
    token       varchar(255) NOT NULL,
    expiry_date timestamp    NOT NULL,
    user_id     bigint       NOT NULL,
    created_at  timestamp    NOT NULL,
    updated_at  timestamp    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS password_history
(
    id           BIGSERIAL PRIMARY KEY,
    old_password varchar(100) NOT NULL,
    changed_at   timestamp    NOT NULL,
    user_id      bigint       NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id)
);