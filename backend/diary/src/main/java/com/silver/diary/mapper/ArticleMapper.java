package com.silver.diary.mapper;

import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.silver.diary.entity.Article;

public interface ArticleMapper extends BaseMapper<Article> {
    @Select("SELECT * FROM article WHERE id = #{id} FOR UPDATE")
    Article lock(Integer id);
}
