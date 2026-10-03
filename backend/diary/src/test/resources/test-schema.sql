CREATE TABLE IF NOT EXISTS `user` (
id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(30) UNIQUE, password VARCHAR(255),
nickname VARCHAR(100), email VARCHAR(254), user_pic VARCHAR(512), role VARCHAR(10) DEFAULT 'USER',
status INT DEFAULT 0, token_version INT DEFAULT 0, create_time TIMESTAMP, update_time TIMESTAMP);

CREATE TABLE IF NOT EXISTS category (id INT AUTO_INCREMENT PRIMARY KEY, cate_name VARCHAR(50), cate_alias VARCHAR(50), create_user INT, create_time TIMESTAMP, update_time TIMESTAMP);
CREATE TABLE IF NOT EXISTS article (id INT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(200), cate_id INT, cover_img VARCHAR(512), content CLOB, state VARCHAR(10), create_user INT, create_time TIMESTAMP, update_time TIMESTAMP);

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
