package com.silver.music.mapper;

import com.silver.music.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    @org.apache.ibatis.annotations.Select("SELECT *, id AS comment_id FROM tb_comment WHERE id = #{id} FOR UPDATE")
    Comment lock(Long id);

}
