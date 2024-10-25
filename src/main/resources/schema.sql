DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     username VARCHAR NOT NULL UNIQUE,
     password VARCHAR NOT NULL,
     role VARCHAR NOT NULL
);

CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR NOT NULL,
    content VARCHAR NOT NULL,
    created_at DATETIME NOT NULL,
    likes INT DEFAULT 0,
    dislikes INT DEFAULT 0,
    user_id BIGINT,
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

INSERT INTO users(username, password, role) VALUES ('alice','$2y$10$1gVNFRAR67kfXKPJcYb68eycGSZAKSQ/AcC3pcxSP7L.dKnHSgDBm', 'ADMIN');
INSERT INTO users(username, password, role) VALUES ('alex','$2y$10$1gVNFRAR67kfXKPJcYb68eycGSZAKSQ/AcC3pcxSP7L.dKnHSgDBm', 'USER');