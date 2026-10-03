package com.silver.music.controller;

import com.silver.diary.exception.BusinessException;

import com.silver.music.dto.*;
import com.silver.music.entity.Artist;
import com.silver.music.entity.Playlist;
import com.silver.music.vo.ArtistNameVO;
import com.silver.music.vo.SongAdminVO;

import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.*;
import com.silver.music.utils.BindingResultUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/music/admin")
public class AdminController {

@Autowired
    private ArtistService artistService;
    @Autowired
    private SongService songService;
    @Autowired
    private PlaylistService playlistService;

    @GetMapping("/getAllArtistsCount")
    public Result<Long> getAllArtistsCount(@RequestParam(required = false) Integer gender, @RequestParam(required = false) String area) {
        return artistService.getAllArtistsCount(gender, area);
    }

    @PostMapping("/getAllArtists")
    public Result<PageResult<Artist>> getAllArtists(@RequestBody ArtistDto artistDto) {
        return artistService.getAllArtistsAndDetail(artistDto);
    }

    @PostMapping("/addArtist")
    public Result addArtist(@RequestBody ArtistAddDto artistAddDto) {
        return artistService.addArtist(artistAddDto);
    }

    @PutMapping("/updateArtist")
    public Result updateArtist(@RequestBody ArtistUpdateDto artistUpdateDto) {
        return artistService.updateArtist(artistUpdateDto);
    }

    @DeleteMapping("/deleteArtist/{id}")
    public Result deleteArtist(@PathVariable("id") Long artistId) {
        return artistService.deleteArtist(artistId);
    }

    @DeleteMapping("/deleteArtists")
    public Result deleteArtists(@RequestBody List<Long> artistIds) {
        return artistService.deleteArtists(artistIds);
    }

    @GetMapping("/getAllSongsCount")
    public Result<Long> getAllSongsCount(@RequestParam(required = false) String style) {
        return songService.getAllSongsCount(style);
    }

    @GetMapping("/getAllArtistNames")
    public Result<List<ArtistNameVO>> getAllArtistNames() {
        return artistService.getAllArtistNames();
    }

    @PostMapping("/getAllSongsByArtist")
    public Result<PageResult<SongAdminVO>> getAllSongsByArtist(@RequestBody SongAndArtistDto songDto) {
        return songService.getAllSongsByArtist(songDto);
    }

    @PostMapping("/addSong")
    public Result addSong(@RequestBody SongAddDto songAddDto) {
        return songService.addSong(songAddDto);
    }

    @PutMapping("/updateSong")
    public Result UpdateSong(@RequestBody SongUpdateDto songUpdateDto) {
        return songService.updateSong(songUpdateDto);
    }

    @DeleteMapping("/deleteSong/{id}")
    public Result deleteSong(@PathVariable("id") Long songId) {
        return songService.deleteSong(songId);
    }

    @DeleteMapping("/deleteSongs")
    public Result deleteSongs(@RequestBody List<Long> songIds) {
        return songService.deleteSongs(songIds);
    }

    @GetMapping("/getAllPlaylistsCount")
    public Result<Long> getAllPlaylistsCount(@RequestParam(required = false) String style) {
        return playlistService.getAllPlaylistsCount(style);
    }

    @PostMapping("/getAllPlaylists")
    public Result<PageResult<Playlist>> getAllPlaylists(@RequestBody PlaylistDto playlistDto) {
        return playlistService.getAllPlaylistsInfo(playlistDto);
    }

    @PostMapping("/addPlaylist")
    public Result addPlaylist(@RequestBody PlaylistAddDto playlistAddDto) {
        return playlistService.addPlaylist(playlistAddDto);
    }

    @PutMapping("/updatePlaylist")
    public Result updatePlaylist(@RequestBody PlaylistUpdateDto playlistUpdateDto) {
        return playlistService.updatePlaylist(playlistUpdateDto);
    }

    @DeleteMapping("/deletePlaylist/{id}")
    public Result deletePlaylist(@PathVariable("id") Long playlistId) {
        return playlistService.deletePlaylist(playlistId);
    }

    @DeleteMapping("/deletePlaylists")
    public Result deletePlaylists(@RequestBody List<Long> playlistIds) {
        return playlistService.deletePlaylists(playlistIds);
    }

}
