package com.silver.music.controller;

import com.silver.music.dto.CommentPlaylistDto;
import com.silver.music.dto.CommentSongDto;
import com.silver.diary.common.Result;
import com.silver.music.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('USER')")
@RestController
@RequestMapping("/music/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/addSongComment")
    public Result addSongComment(@RequestBody CommentSongDto commentSongDto) {
        return commentService.addSongComment(commentSongDto);
    }

    @PostMapping("/addPlaylistComment")
    public Result addPlaylistComment(@RequestBody CommentPlaylistDto commentPlaylistDto) {
        return commentService.addPlaylistComment(commentPlaylistDto);
    }

    @PatchMapping("/likeComment/{id}")
    public Result likeComment(@PathVariable("id") Long commentId) {
        return commentService.likeComment(commentId);
    }

    @PatchMapping("/cancelLikeComment/{id}")
    public Result cancelLikeComment(@PathVariable("id") Long commentId) {
        return commentService.cancelLikeComment(commentId);
    }

    @DeleteMapping("/deleteComment/{id}")
    public Result deleteComment(@PathVariable("id") Long commentId) {
        return commentService.deleteComment(commentId);
    }

}
