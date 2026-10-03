package com.silver.music.service;

import com.silver.music.dto.SongAddDto;
import com.silver.music.dto.SongAndArtistDto;
import com.silver.music.dto.SongDto;
import com.silver.music.dto.SongUpdateDto;
import com.silver.music.entity.Song;
import com.silver.music.vo.SongAdminVO;
import com.silver.music.vo.SongDetailVO;
import com.silver.music.vo.SongVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.baomidou.mybatisplus.spring.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface SongService extends IService<Song> {

    Result<PageResult<SongVO>> getAllSongs(SongDto songDto, HttpServletRequest request);

    Result<PageResult<SongAdminVO>> getAllSongsByArtist(SongAndArtistDto songDto);

    Result<List<SongVO>> getRecommendedSongs(HttpServletRequest request);

    Result<SongDetailVO> getSongDetail(Long songId, HttpServletRequest request);

    Result<Long> getAllSongsCount(String style);

    Result<Void> addSong(SongAddDto songAddDto);

    Result<Void> updateSong(SongUpdateDto songUpdateDto);

    Result<Void> updateSongCover(Long songId, String coverUrl);

    Result<Void> updateSongAudio(Long songId, String audioUrl, String duration);

    Result<Void> deleteSong(Long songId);

    Result<Void> deleteSongs(List<Long> songIds);

}
