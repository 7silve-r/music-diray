package com.silver.music.service.impl;

import com.silver.diary.exception.BusinessException;

import com.silver.music.constant.JwtClaimsConstant;
import com.silver.music.constant.MessageConstant;
import com.silver.music.mapper.FeedbackMapper;
import com.silver.music.dto.FeedbackDto;
import com.silver.music.entity.Feedback;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.FeedbackService;
import com.silver.music.utils.CurrentUserUtil;
import com.silver.music.utils.TypeConversionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service

public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public Result<PageResult<Feedback>> getAllFeedbacks(FeedbackDto feedbackDto) {

        Page<Feedback> page = new Page<>(feedbackDto.getPageNum(), feedbackDto.getPageSize());
        QueryWrapper<Feedback> queryWrapper = new QueryWrapper<>();
        if (feedbackDto.getKeyword() != null) {
            queryWrapper.like("feedback", feedbackDto.getKeyword());
        }

        queryWrapper.orderByDesc("create_time");

        IPage<Feedback> feedbackPage = feedbackMapper.selectPage(page, queryWrapper);
        if (feedbackPage.getRecords().size() == 0) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, new PageResult<>(0L, null));
        }

        return Result.success(new PageResult<>(feedbackPage.getTotal(), feedbackPage.getRecords()));
    }

    @Override
    public Result<Void> deleteFeedback(Long feedbackId) {
        if (feedbackMapper.deleteById(feedbackId) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }

    @Override
    public Result<Void> deleteFeedbacks(List<Long> feedbackIds) {
        if (feedbackMapper.deleteByIds(feedbackIds) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }

    @Override
    public Result<Void> addFeedback(String content) {
        if (content == null || content.isBlank() || content.length() > 255) throw new BusinessException("反馈须为1到255字");
        Map<String, Object> map = CurrentUserUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setFeedback(content);
        feedback.setCreateTime(LocalDateTime.now());

        if (feedbackMapper.insert(feedback) == 0) {
            throw new BusinessException(MessageConstant.ADD + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS, null);
    }

}
