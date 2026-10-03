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

    Result addSong(SongAddDto songAddDto);

    Result updateSong(SongUpdateDto songUpdateDto);

    Result updateSongCover(Long songId, String coverUrl);

    Result updateSongAudio(Long songId, String audioUrl, String duration);

    Result deleteSong(Long songId);

    Result deleteSongs(List<Long> songIds);

}
