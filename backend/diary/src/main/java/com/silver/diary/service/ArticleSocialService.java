package com.silver.diary.service;

import com.silver.diary.common.PageResult;
import com.silver.diary.vo.ArticleVO;
import com.silver.diary.vo.ArticleCommentVO;

public interface ArticleSocialService {
    PageResult<ArticleVO> list(Integer pageNum, Integer pageSize, boolean favorites);
    ArticleVO detail(Integer id);
    void like(Integer id, boolean cancel);
    void collect(Integer id, boolean cancel);
    void comment(Integer id, String content);
    PageResult<ArticleCommentVO> comments(Integer id, Integer pageNum, Integer pageSize);
    void likeComment(Integer id, boolean cancel);
    void deleteComment(Integer id);
    void delete(Integer id);
}
