-- Only creates missing tables. Run against a NEW music_diary database.
CREATE DATABASE IF NOT EXISTS music_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE music_diary;
CREATE TABLE IF NOT EXISTS `user` (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL UNIQUE,
  role VARCHAR(10) NOT NULL DEFAULT 'USER',
  status INT NOT NULL DEFAULT 0,
  token_version INT NOT NULL DEFAULT 0,
  password VARCHAR(255) NOT NULL,
  nickname VARCHAR(100),
  email VARCHAR(254) UNIQUE,
  email_verified BOOLEAN NOT NULL DEFAULT FALSE,
  user_pic VARCHAR(512),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS category (
  id INT PRIMARY KEY AUTO_INCREMENT,
  cate_name VARCHAR(50) NOT NULL, cate_alias VARCHAR(50) NOT NULL,
  create_user INT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT category_owner FOREIGN KEY (create_user) REFERENCES `user`(id) ON DELETE CASCADE,
  INDEX category_user (create_user)
);
CREATE TABLE IF NOT EXISTS article (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  cate_id INT NOT NULL,
  cover_img VARCHAR(512),
  content MEDIUMTEXT NOT NULL,
  state VARCHAR(10) NOT NULL DEFAULT '私有',
  create_user INT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT article_owner FOREIGN KEY (create_user) REFERENCES `user`(id) ON DELETE CASCADE,
  CONSTRAINT article_category FOREIGN KEY (cate_id) REFERENCES category(id) ON DELETE CASCADE,
  INDEX article_user_time (create_user, create_time),
  INDEX article_user_category_state (create_user, cate_id, state)
);

CREATE TABLE IF NOT EXISTS article_like (
    id INT PRIMARY KEY AUTO_INCREMENT,
    article_id INT NOT NULL,
    user_id INT NOT NULL,

    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
    ,
    UNIQUE (article_id, user_id)
);

CREATE TABLE IF NOT EXISTS article_favorite (
    id INT PRIMARY KEY AUTO_INCREMENT,
    article_id INT NOT NULL,
    user_id INT NOT NULL,

    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
    ,
    UNIQUE (article_id, user_id)
);

CREATE TABLE IF NOT EXISTS article_comment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    article_id INT NOT NULL,
    user_id INT NOT NULL,
    content VARCHAR(255) NOT NULL, create_time TIMESTAMP NOT NULL,
    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE


);

CREATE TABLE IF NOT EXISTS article_comment_like (
    id INT PRIMARY KEY AUTO_INCREMENT,
    comment_id INT NOT NULL,
    user_id INT NOT NULL,

    FOREIGN KEY (comment_id) REFERENCES article_comment(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE
    ,
    UNIQUE (comment_id, user_id)
);

CREATE TABLE IF NOT EXISTS email_code (
  id INT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(254) NOT NULL,
  purpose VARCHAR(10) NOT NULL,
  code VARCHAR(255) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  sent_at TIMESTAMP NOT NULL,
  attempts INT NOT NULL DEFAULT 0,
  UNIQUE (email, purpose)
);
