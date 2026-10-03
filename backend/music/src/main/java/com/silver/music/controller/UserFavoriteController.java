package com.silver.music.controller;

import com.silver.music.dto.PlaylistDto;
import com.silver.music.dto.SongDto;
import com.silver.music.vo.PlaylistVO;
import com.silver.music.vo.SongVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.UserFavoriteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('USER')")
@RestController
@RequestMapping("/music/favorite")
public class UserFavoriteController {

    @Autowired
    private UserFavoriteService userFavoriteService;

    @PostMapping("/getFavoriteSongs")
    public Result<PageResult<SongVO>> getUserFavoriteSongs(@RequestBody @Valid SongDto songDto) {
        return userFavoriteService.getUserFavoriteSongs(songDto);
    }

    @PostMapping("/collectSong")
    public Result<Void> collectSong(@RequestParam Long songId) {
        return userFavoriteService.collectSong(songId);
    }

    @DeleteMapping("/cancelCollectSong")
    public Result<Void> cancelCollectSong(@RequestParam Long songId) {
        return userFavoriteService.cancelCollectSong(songId);
    }

    @PostMapping("/getFavoritePlaylists")
    public Result<PageResult<PlaylistVO>> getFavoritePlaylists(@RequestBody @Valid PlaylistDto playlistDto) {
        return userFavoriteService.getUserFavoritePlaylists(playlistDto);
    }

    @PostMapping("/collectPlaylist")
    public Result<Void> collectPlaylist(@RequestParam Long playlistId) {
        return userFavoriteService.collectPlaylist(playlistId);
    }

    @DeleteMapping("/cancelCollectPlaylist")
    public Result<Void> cancelCollectPlaylist(@RequestParam Long playlistId) {
        return userFavoriteService.cancelCollectPlaylist(playlistId);
    }
}
