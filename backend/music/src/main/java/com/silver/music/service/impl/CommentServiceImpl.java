package com.silver.music.service.impl;

import com.silver.diary.exception.BusinessException;

import com.silver.music.constant.JwtClaimsConstant;
import com.silver.music.constant.MessageConstant;
import com.silver.music.mapper.CommentMapper;
import com.silver.music.dto.CommentPlaylistDto;
import com.silver.music.dto.CommentSongDto;
import com.silver.music.entity.Comment;
import com.silver.diary.common.Result;
import com.silver.music.service.CommentService;
import com.silver.music.utils.CurrentUserUtil;
import com.silver.music.utils.TypeConversionUtil;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override

    public Result addSongComment(CommentSongDto commentSongDto) {
        Map<String, Object> map = CurrentUserUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setSongId(commentSongDto.getSongId());
        comment.setContent(commentSongDto.getContent());
        comment.setType(0);
        comment.setCreateTime(LocalDateTime.now());
        comment.setLikeCount(0L);

        if (commentMapper.insert(comment) == 0) {
            throw new BusinessException(MessageConstant.ADD + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS, null);
    }

    @Override

    public Result addPlaylistComment(CommentPlaylistDto commentPlaylistDto) {
        Map<String, Object> map = CurrentUserUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPlaylistId(commentPlaylistDto.getPlaylistId());
        comment.setContent(commentPlaylistDto.getContent());
        comment.setType(1);
        comment.setCreateTime(LocalDateTime.now());
        comment.setLikeCount(0L);

        if (commentMapper.insert(comment) == 0) {
            throw new BusinessException(MessageConstant.ADD + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS, null);
    }

    @Override

    public Result likeComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        if (comment.getLikeCount() == null) {
            comment.setLikeCount(0L);
        }
        comment.setLikeCount(comment.getLikeCount() + 1);

        if (commentMapper.updateById(comment) == 0) {
            throw new BusinessException(MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.SUCCESS, null);
    }

    @Override

    public Result cancelLikeComment(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        if (comment.getLikeCount() == null) {
            comment.setLikeCount(0L);
        }
        comment.setLikeCount(comment.getLikeCount() - 1);

        if (commentMapper.updateById(comment) == 0) {
            throw new BusinessException(MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.SUCCESS, null);
    }

    @Override

    public Result deleteComment(Long commentId) {
        Map<String, Object> map = CurrentUserUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(MessageConstant.NOT_FOUND);
        }
        if (!Objects.equals(comment.getUserId(), userId)) {
            throw new BusinessException(MessageConstant.NO_PERMISSION);
        }

        if (commentMapper.deleteById(commentId) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }
}
