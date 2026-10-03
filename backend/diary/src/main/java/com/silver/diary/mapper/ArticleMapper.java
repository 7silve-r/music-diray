package com.silver.diary.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.silver.diary.entity.Article;

public interface ArticleMapper extends BaseMapper<Article> {
    @org.apache.ibatis.annotations.Select("SELECT * FROM article WHERE id = #{id} FOR UPDATE")
    Article lock(Integer id);
}
