package com.silver.music.service;

import com.silver.music.dto.ArtistAddDto;
import com.silver.music.dto.ArtistDto;
import com.silver.music.dto.ArtistUpdateDto;
import com.silver.music.entity.Artist;
import com.silver.music.vo.ArtistDetailVO;
import com.silver.music.vo.ArtistNameVO;
import com.silver.music.vo.ArtistVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.baomidou.mybatisplus.spring.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface ArtistService extends IService<Artist> {

    Result<PageResult<ArtistVO>> getAllArtists(ArtistDto artistDto);

    Result<PageResult<Artist>> getAllArtistsAndDetail(ArtistDto artistDto);

    Result<List<ArtistNameVO>> getAllArtistNames();

    Result<List<ArtistVO>> getRandomArtists();

    Result<ArtistDetailVO> getArtistDetail(Long artistId, HttpServletRequest request);

    Result<Long> getAllArtistsCount(Integer gender, String area);

    Result<Void> addArtist(ArtistAddDto artistAddDto);

    Result<Void> updateArtist(ArtistUpdateDto artistUpdateDto);

    Result<Void> updateArtistAvatar(Long artistId, String avatar);

    Result<Void> deleteArtist(Long ArtistId);

    Result<Void> deleteArtists(List<Long> artistIds);

}
