package com.silver.diary.vo;

import com.silver.diary.entity.Article;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true) // 继承父类字段
public class ArticleVO extends Article {
    private String cateName;
    private String authorAvatar;
    private String authorNickname;
    public ArticleVO(Article article) {
        super.setId(article.getId());
        super.setTitle(article.getTitle());
        super.setCateId(article.getCateId());
        super.setContent(article.getContent());
        super.setCoverImg(article.getCoverImg());
        super.setState(article.getState());
        super.setCreateUser(article.getCreateUser());
        super.setCreateTime(article.getCreateTime());
        super.setUpdateTime(article.getUpdateTime());
    }
}
