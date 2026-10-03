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
    public Result<PageResult<Artist>> getAllArtists(@RequestBody @Valid ArtistDto artistDto) {
        return artistService.getAllArtistsAndDetail(artistDto);
    }

    @PostMapping("/addArtist")
    public Result<Void> addArtist(@RequestBody @Valid ArtistAddDto artistAddDto) {
        return artistService.addArtist(artistAddDto);
    }

    @PutMapping("/updateArtist")
    public Result<Void> updateArtist(@RequestBody @Valid ArtistUpdateDto artistUpdateDto) {
        return artistService.updateArtist(artistUpdateDto);
    }

    @DeleteMapping("/deleteArtist/{id}")
    public Result<Void> deleteArtist(@PathVariable("id") Long artistId) {
        return artistService.deleteArtist(artistId);
    }

    @DeleteMapping("/deleteArtists")
    public Result<Void> deleteArtists(@RequestBody List<Long> artistIds) {
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
    public Result<PageResult<SongAdminVO>> getAllSongsByArtist(@RequestBody @Valid SongAndArtistDto songDto) {
        return songService.getAllSongsByArtist(songDto);
    }

    @PostMapping("/addSong")
    public Result<Void> addSong(@RequestBody @Valid SongAddDto songAddDto) {
        return songService.addSong(songAddDto);
    }

    @PutMapping("/updateSong")
    public Result<Void> updateSong(@RequestBody @Valid SongUpdateDto songUpdateDto) {
        return songService.updateSong(songUpdateDto);
    }

    @DeleteMapping("/deleteSong/{id}")
    public Result<Void> deleteSong(@PathVariable("id") Long songId) {
        return songService.deleteSong(songId);
    }

    @DeleteMapping("/deleteSongs")
    public Result<Void> deleteSongs(@RequestBody List<Long> songIds) {
        return songService.deleteSongs(songIds);
    }

    @GetMapping("/getAllPlaylistsCount")
    public Result<Long> getAllPlaylistsCount(@RequestParam(required = false) String style) {
        return playlistService.getAllPlaylistsCount(style);
    }

    @PostMapping("/getAllPlaylists")
    public Result<PageResult<Playlist>> getAllPlaylists(@RequestBody @Valid PlaylistDto playlistDto) {
        return playlistService.getAllPlaylistsInfo(playlistDto);
    }

    @PostMapping("/addPlaylist")
    public Result<Void> addPlaylist(@RequestBody @Valid PlaylistAddDto playlistAddDto) {
        return playlistService.addPlaylist(playlistAddDto);
    }

    @PutMapping("/updatePlaylist")
    public Result<Void> updatePlaylist(@RequestBody @Valid PlaylistUpdateDto playlistUpdateDto) {
        return playlistService.updatePlaylist(playlistUpdateDto);
    }

    @DeleteMapping("/deletePlaylist/{id}")
    public Result<Void> deletePlaylist(@PathVariable("id") Long playlistId) {
        return playlistService.deletePlaylist(playlistId);
    }

    @DeleteMapping("/deletePlaylists")
    public Result<Void> deletePlaylists(@RequestBody List<Long> playlistIds) {
        return playlistService.deletePlaylists(playlistIds);
    }

}
