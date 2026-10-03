package com.silver.music.service;

import com.silver.music.dto.CommentPlaylistDto;
import com.silver.music.dto.CommentSongDto;
import com.silver.music.entity.Comment;
import com.silver.diary.common.Result;
import com.baomidou.mybatisplus.spring.service.IService;

public interface CommentService extends IService<Comment> {

    Result addSongComment(CommentSongDto commentSongDto);

    Result addPlaylistComment(CommentPlaylistDto commentPlaylistDto);

    Result likeComment(Long commentId);

    Result cancelLikeComment(Long commentId);

    Result deleteComment(Long commentId);

}
