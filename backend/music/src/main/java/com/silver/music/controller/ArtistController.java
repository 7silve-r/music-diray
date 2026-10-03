package com.silver.music.controller;

import com.silver.music.dto.ArtistDto;
import com.silver.music.vo.ArtistDetailVO;
import com.silver.music.vo.ArtistVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.ArtistService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/music/public/artist")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    @PostMapping("/getAllArtists")
    public Result<PageResult<ArtistVO>> getAllArtists(@RequestBody @Valid ArtistDto artistDto) {
        return artistService.getAllArtists(artistDto);
    }

    @GetMapping("/getRandomArtists")
    public Result<List<ArtistVO>> getRandomArtists() {
        return artistService.getRandomArtists();
    }

    @GetMapping("/getArtistDetail/{id}")
    public Result<ArtistDetailVO> getArtistDetail(@PathVariable("id") Long artistId, HttpServletRequest request) {
        return artistService.getArtistDetail(artistId, request);
    }

}
