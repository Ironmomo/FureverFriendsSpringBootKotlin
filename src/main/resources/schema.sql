DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS user_following;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
     username VARCHAR NOT NULL PRIMARY KEY ,
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
    user_id VARCHAR,
    CONSTRAINT fk_post_user FOREIGN KEY (user_id) REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE user_following (
    follower_id VARCHAR NOT NULL,
    following_id VARCHAR NOT NULL,
    status VARCHAR NOT NULL,
    CONSTRAINT pk_user_following PRIMARY KEY (follower_id, following_id),
    CONSTRAINT fk_follower FOREIGN KEY (follower_id) REFERENCES users(username) ON DELETE CASCADE,
    CONSTRAINT fk_following FOREIGN KEY (following_id) REFERENCES users(username) ON DELETE CASCADE
);

INSERT INTO users(username, password, role) VALUES ('alice','$2y$10$1gVNFRAR67kfXKPJcYb68eycGSZAKSQ/AcC3pcxSP7L.dKnHSgDBm', 'ADMIN');
INSERT INTO users(username, password, role) VALUES ('alex','$2y$10$1gVNFRAR67kfXKPJcYb68eycGSZAKSQ/AcC3pcxSP7L.dKnHSgDBm', 'USER');