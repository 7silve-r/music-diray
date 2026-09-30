-- Only creates missing tables. Run against a NEW music_diary database.
CREATE DATABASE IF NOT EXISTS vibe_diary CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE vibe_diary;
CREATE TABLE IF NOT EXISTS `user` (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(30) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  nickname VARCHAR(100),
  email VARCHAR(254),
  user_pic VARCHAR(512),
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS 'category' (
  id INT PRIMARY KEY AUTO_INCREMENT,
  cate_name VARCHAR(50) NOT NULL, cate_alias VARCHAR(50) NOT NULL,
  create_user INT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT category_owner FOREIGN KEY (create_user) REFERENCES `user`(id),
  INDEX category_user (create_user)
);
CREATE TABLE IF NOT EXISTS 'article' (
  id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL, cate_id INT NOT NULL, cover_img VARCHAR(512),
  content MEDIUMTEXT NOT NULL, state VARCHAR(10) NOT NULL DEFAULT '草稿',
  create_user INT NOT NULL,
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT article_owner FOREIGN KEY (create_user) REFERENCES `user`(id),
  CONSTRAINT article_category FOREIGN KEY (cate_id) REFERENCES category(id),
  INDEX article_user_time (create_user, create_time),
  INDEX article_user_category_state (create_user, cate_id, state)
);