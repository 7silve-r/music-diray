package com.silver.music.controller;

import com.silver.music.dto.SongDto;
import com.silver.music.vo.SongDetailVO;
import com.silver.music.vo.SongVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.SongService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/music/public/song")
public class SongController {

    @Autowired
    private SongService songService;

    @PostMapping("/getAllSongs")
    public Result<PageResult<SongVO>> getAllSongs(@RequestBody @Valid SongDto songDto, HttpServletRequest request) {
        return songService.getAllSongs(songDto, request);
    }

    @GetMapping("/getRecommendedSongs")
    public Result<List<SongVO>> getRecommendedSongs(HttpServletRequest request) {
        return songService.getRecommendedSongs(request);
    }

    @GetMapping("/getSongDetail/{id}")
    public Result<SongDetailVO> getSongDetail(@PathVariable("id") Long songId, HttpServletRequest request) {
        return songService.getSongDetail(songId, request);
    }

}
