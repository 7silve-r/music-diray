package com.silver.music.service;

import com.silver.music.dto.FeedbackDto;
import com.silver.music.entity.Feedback;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

public interface FeedbackService extends IService<Feedback> {

    Result<PageResult<Feedback>> getAllFeedbacks(FeedbackDto feedbackDto);

    Result<Void> deleteFeedback(Long feedbackId);

    Result<Void> deleteFeedbacks(List<Long> feedbackIds);

    Result<Void> addFeedback(String content);

}
