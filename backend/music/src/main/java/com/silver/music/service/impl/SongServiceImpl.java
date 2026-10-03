package com.silver.music.service.impl;

import com.silver.diary.exception.BusinessException;

import com.silver.music.constant.JwtClaimsConstant;
import com.silver.music.constant.MessageConstant;
import com.silver.music.enumeration.LikeStatusEnum;
import com.silver.music.enumeration.RoleEnum;
import com.silver.music.mapper.GenreMapper;
import com.silver.music.mapper.SongMapper;
import com.silver.music.mapper.StyleMapper;
import com.silver.music.mapper.UserFavoriteMapper;
import com.silver.music.dto.SongAddDto;
import com.silver.music.dto.SongAndArtistDto;
import com.silver.music.dto.SongDto;
import com.silver.music.dto.SongUpdateDto;
import com.silver.music.entity.Genre;
import com.silver.music.entity.Song;
import com.silver.music.entity.Style;
import com.silver.music.entity.UserFavorite;
import com.silver.music.vo.SongAdminVO;
import com.silver.music.vo.SongDetailVO;
import com.silver.music.vo.SongVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.SongService;
import com.silver.music.service.MinioService;
import com.silver.music.utils.CurrentUserUtil;
import com.silver.music.utils.TypeConversionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service

public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    @Autowired
    private SongMapper songMapper;
    @Autowired
    private UserFavoriteMapper userFavoriteMapper;
    @Autowired
    private StyleMapper styleMapper;
    @Autowired
    private GenreMapper genreMapper;
    @Autowired
    private MinioService minioService;

    @Override
    public Result<PageResult<SongVO>> getAllSongs(SongDto songDto, HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Map<String, Object> map = null;
        if (token != null && !token.isEmpty()) {
            map = CurrentUserUtil.get();
        }

        Page<SongVO> page = new Page<>(songDto.getPageNum(), songDto.getPageSize());
        IPage<SongVO> songPage = songMapper.getSongsWithArtist(page, songDto.getSongName(), songDto.getArtistName(), songDto.getAlbum());
        if (songPage.getRecords().isEmpty()) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, new PageResult<>(0L, null));
        }

        List<SongVO> songVOList = songPage.getRecords().stream()
                .peek(songVO -> songVO.setLikeStatus(LikeStatusEnum.DEFAULT.getId()))
                .toList();

        if (map != null) {
            String role = (String) map.get(JwtClaimsConstant.ROLE);
            if ((role.equals(RoleEnum.USER.getRole()) || role.equals(RoleEnum.ADMIN.getRole()))) {
                Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
                Long userId = TypeConversionUtil.toLong(userIdObj);

                List<UserFavorite> favoriteSongs = userFavoriteMapper.selectList(new QueryWrapper<UserFavorite>()
                        .eq("user_id", userId)
                        .eq("type", 0));

                Set<Long> favoriteSongIds = favoriteSongs.stream()
                        .map(UserFavorite::getSongId)
                        .collect(Collectors.toSet());

                for (SongVO songVO : songVOList) {
                    if (favoriteSongIds.contains(songVO.getSongId())) {
                        songVO.setLikeStatus(LikeStatusEnum.LIKE.getId());
                    }
                }
            }
        }

        return Result.success(new PageResult<>(songPage.getTotal(), songVOList));
    }

    @Override

    public Result<PageResult<SongAdminVO>> getAllSongsByArtist(SongAndArtistDto songDto) {

        Page<SongAdminVO> page = new Page<>(songDto.getPageNum(), songDto.getPageSize());
        IPage<SongAdminVO> songPage = songMapper.getSongsWithArtistName(page, songDto.getArtistId(), songDto.getSongName(), songDto.getAlbum());

        if (songPage.getRecords().isEmpty()) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, new PageResult<>(0L, null));
        }

        return Result.success(new PageResult<>(songPage.getTotal(), songPage.getRecords()));
    }

    @Override
    public Result<List<SongVO>> getRecommendedSongs(HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Map<String, Object> map = null;
        if (token != null && !token.isEmpty()) {
            map = CurrentUserUtil.get();
        }

        if (map == null) {
            return Result.success(songMapper.getRandomSongsWithArtist());
        }

        Long userId = TypeConversionUtil.toLong(map.get(JwtClaimsConstant.USER_ID));

        List<Long> favoriteSongIds = userFavoriteMapper.getFavoriteSongIdsByUserId(userId);
        if (favoriteSongIds.isEmpty()) {
            return Result.success(songMapper.getRandomSongsWithArtist());
        }

        List<Long> favoriteStyleIds = songMapper.getFavoriteSongStyles(favoriteSongIds);
        Map<Long, Long> styleFrequency = favoriteStyleIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<Long> sortedStyleIds = styleFrequency.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<SongVO> cachedSongs = sortedStyleIds.isEmpty() ? new ArrayList<>()
                : new ArrayList<>(songMapper.getRecommendedSongsByStyles(sortedStyleIds, favoriteSongIds, 80));

        Collections.shuffle(cachedSongs);
        List<SongVO> recommendedSongs = cachedSongs.subList(0, Math.min(20, cachedSongs.size()));

        if (recommendedSongs.size() < 20) {
            List<SongVO> randomSongs = songMapper.getRandomSongsWithArtist();
            Set<Long> addedSongIds = recommendedSongs.stream().map(SongVO::getSongId).collect(Collectors.toSet());
            for (SongVO song : randomSongs) {
                if (recommendedSongs.size() >= 20) break;
                if (!addedSongIds.contains(song.getSongId())) {
                    recommendedSongs.add(song);
                    addedSongIds.add(song.getSongId());
                }
            }
        }

        return Result.success(recommendedSongs);
    }

    @Override
    public Result<SongDetailVO> getSongDetail(Long songId, HttpServletRequest request) {
        SongDetailVO songDetailVO = songMapper.getSongDetailById(songId);
        if (songDetailVO == null) throw new BusinessException(404, "歌曲不存在");

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Map<String, Object> map = null;
        if (token != null && !token.isEmpty()) {
            map = CurrentUserUtil.get();
        }

        if (map != null) {
            String role = (String) map.get(JwtClaimsConstant.ROLE);
            if ((role.equals(RoleEnum.USER.getRole()) || role.equals(RoleEnum.ADMIN.getRole()))) {
                Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
                Long userId = TypeConversionUtil.toLong(userIdObj);

                UserFavorite favoriteSong = userFavoriteMapper.selectOne(new QueryWrapper<UserFavorite>()
                        .eq("user_id", userId)
                        .eq("type", 0)
                        .eq("song_id", songId));
                if (favoriteSong != null) {
                    songDetailVO.setLikeStatus(LikeStatusEnum.LIKE.getId());
                }
            }
        }

        return Result.success(songDetailVO);
    }

    @Override
    public Result<Long> getAllSongsCount(String style) {
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        if (style != null) {
            queryWrapper.like("style", style);
        }

        return Result.success(songMapper.selectCount(queryWrapper));
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> addSong(SongAddDto songAddDto) {
        Song song = new Song();
        BeanUtils.copyProperties(songAddDto, song);

        if (songMapper.insert(song) == 0) {
            throw new BusinessException(MessageConstant.ADD + MessageConstant.FAILED);
        }

        Long songId = song.getSongId();

        String styleStr = songAddDto.getStyle();
        if (styleStr != null && !styleStr.isEmpty()) {
            List<String> styles = Arrays.asList(styleStr.split(","));

            List<Style> styleList = styleMapper.selectList(new QueryWrapper<Style>().in("name", styles));

            for (Style style : styleList) {
                Genre genre = new Genre();
                genre.setSongId(songId);
                genre.setStyleId(style.getStyleId());
                genreMapper.insert(genre);
            }
        }

        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS, null);
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> updateSong(SongUpdateDto songUpdateDto) {

        Song songInDB = songMapper.selectById(songUpdateDto.getSongId());
        if (songInDB == null) {
            throw new BusinessException(MessageConstant.SONG + MessageConstant.NOT_FOUND);
        }

        Song song = new Song();
        BeanUtils.copyProperties(songUpdateDto, song);
        if (songMapper.updateById(song) == 0) {
            throw new BusinessException(MessageConstant.UPDATE + MessageConstant.FAILED);
        }

        Long songId = songUpdateDto.getSongId();

        genreMapper.delete(new QueryWrapper<Genre>().eq("song_id", songId));

        String styleStr = songUpdateDto.getStyle();
        if (styleStr != null && !styleStr.isEmpty()) {
            List<String> styles = Arrays.asList(styleStr.split(","));

            List<Style> styleList = styleMapper.selectList(new QueryWrapper<Style>().in("name", styles));

            for (Style style : styleList) {
                Genre genre = new Genre();
                genre.setSongId(songId);
                genre.setStyleId(style.getStyleId());
                genreMapper.insert(genre);
            }
        }

        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS, null);
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> updateSongCover(Long songId, String coverUrl) {
        Song song = songMapper.selectById(songId);
        if (song == null) throw new BusinessException(404, "资源不存在");
        String cover = song.getCoverUrl();

        song.setCoverUrl(coverUrl);
        if (songMapper.updateById(song) == 0) {
            throw new BusinessException(MessageConstant.UPDATE + MessageConstant.FAILED);
        }

        com.silver.music.upload.UploadCleanup.afterCommit(minioService, cover);
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS, null);
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> updateSongAudio(Long songId, String audioUrl, String duration) {
        Song song = songMapper.selectById(songId);
        if (song == null) throw new BusinessException(404, "资源不存在");
        String audio = song.getAudioUrl();

        song.setAudioUrl(audioUrl);
        song.setDuration(duration);
        if (songMapper.updateById(song) == 0) {
            throw new BusinessException(MessageConstant.UPDATE + MessageConstant.FAILED);
        }

        com.silver.music.upload.UploadCleanup.afterCommit(minioService, audio);
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS, null);
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> deleteSong(Long songId) {
        Song song = songMapper.selectById(songId);
        if (song == null) {
            throw new BusinessException(MessageConstant.SONG + MessageConstant.NOT_FOUND);
        }
        String cover = song.getCoverUrl();
        String audio = song.getAudioUrl();

        if (cover != null && !cover.isEmpty()) {
            com.silver.music.upload.UploadCleanup.afterCommit(minioService, cover);
        }
        if (audio != null && !audio.isEmpty()) {
            com.silver.music.upload.UploadCleanup.afterCommit(minioService, audio);
        }

        if (songMapper.deleteById(songId) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> deleteSongs(List<Long> songIds) {

        List<Song> songs = songMapper.selectByIds(songIds);
        List<String> coverUrlList = songs.stream()
                .map(Song::getCoverUrl)
                .filter(coverUrl -> coverUrl != null && !coverUrl.isEmpty())
                .toList();
        List<String> audioUrlList = songs.stream()
                .map(Song::getAudioUrl)
                .filter(audioUrl -> audioUrl != null && !audioUrl.isEmpty())
                .toList();

        for (String coverUrl : coverUrlList) {
            com.silver.music.upload.UploadCleanup.afterCommit(minioService, coverUrl);
        }
        for (String audioUrl : audioUrlList) {
            com.silver.music.upload.UploadCleanup.afterCommit(minioService, audioUrl);
        }

        if (songMapper.deleteByIds(songIds) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }

}
