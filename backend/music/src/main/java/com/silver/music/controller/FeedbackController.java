package com.silver.music.controller;

import jakarta.validation.Valid;

import com.silver.music.dto.FeedbackDto;
import com.silver.music.entity.Feedback;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PostMapping("/music/admin/getAllFeedbacks")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<Feedback>> getAllFeedbacks(@RequestBody @Valid FeedbackDto feedbackDto) {
        return feedbackService.getAllFeedbacks(feedbackDto);
    }

    @DeleteMapping("/music/admin/deleteFeedback/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteFeedback(@PathVariable("id") Long feedbackId) {
        return feedbackService.deleteFeedback(feedbackId);
    }

    @DeleteMapping("/music/admin/deleteFeedbacks")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteFeedbacks(@RequestBody List<Long> feedbackIds) {
        return feedbackService.deleteFeedbacks(feedbackIds);
    }

    @PostMapping("/music/feedback/addFeedback")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> addFeedback(@RequestParam(value = "content") String content) {
        return feedbackService.addFeedback(content);
    }

}
