package com.silver.music.service.impl;

import com.silver.diary.exception.BusinessException;

import com.silver.music.constant.JwtClaimsConstant;
import com.silver.music.constant.MessageConstant;
import com.silver.music.enumeration.LikeStatusEnum;
import com.silver.music.mapper.PlaylistMapper;
import com.silver.music.mapper.SongMapper;
import com.silver.music.mapper.UserFavoriteMapper;
import com.silver.music.dto.PlaylistDto;
import com.silver.music.dto.SongDto;
import com.silver.music.entity.Playlist;
import com.silver.music.entity.UserFavorite;
import com.silver.music.vo.PlaylistVO;
import com.silver.music.vo.SongVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.UserFavoriteService;
import com.silver.music.utils.CurrentUserUtil;
import com.silver.music.utils.TypeConversionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service

public class UserFavoriteServiceImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements UserFavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;
    @Autowired
    private SongMapper songMapper;
    @Autowired
    private PlaylistMapper playlistMapper;

    @Override
    public Result<PageResult<SongVO>> getUserFavoriteSongs(SongDto songDto) {
        Map<String, Object> map = CurrentUserUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        List<Long> favoriteSongIds = userFavoriteMapper.getUserFavoriteSongIds(userId);
        if (favoriteSongIds.isEmpty()) {
            return Result.success(new PageResult<>(0L, Collections.emptyList()));
        }

        Page<SongVO> page = new Page<>(songDto.getPageNum(), songDto.getPageSize());
        IPage<SongVO> songPage = songMapper.getSongsByIds(
                page,
                favoriteSongIds,
                songDto.getSongName(),
                songDto.getArtistName(),
                songDto.getAlbum()
        );

        List<SongVO> songVOList = songPage.getRecords().stream()
                .peek(songVO -> songVO.setLikeStatus(LikeStatusEnum.LIKE.getId()))
                .toList();

        return Result.success(new PageResult<>(songPage.getTotal(), songVOList));
    }

    @Override

    public Result collectSong(Long songId) {
        Map<String, Object> map = CurrentUserUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("type", 0).eq("song_id", songId);
        if (userFavoriteMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(MessageConstant.ADD + MessageConstant.FAILED);
        }

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUserId(userId);
        userFavorite.setType(0);
        userFavorite.setSongId(songId);
        userFavorite.setCreateTime(LocalDateTime.now());
        userFavoriteMapper.insert(userFavorite);

        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS, null);
    }

    @Override

    public Result cancelCollectSong(Long songId) {
        Map<String, Object> map = CurrentUserUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("type", 0).eq("song_id", songId);
        if (userFavoriteMapper.delete(queryWrapper) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }

    @Override
    public Result<PageResult<PlaylistVO>> getUserFavoritePlaylists(PlaylistDto playlistDto) {
        Map<String, Object> map = CurrentUserUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        List<Long> favoritePlaylistIds = userFavoriteMapper.getUserFavoritePlaylistIds(userId);
        if (favoritePlaylistIds.isEmpty()) {
            return Result.success(new PageResult<>(0L, Collections.emptyList()));
        }

        Page<PlaylistVO> page = new Page<>(playlistDto.getPageNum(), playlistDto.getPageSize());
        IPage<PlaylistVO> playlistPage = playlistMapper.getPlaylistsByIds(
                userId,
                page,
                favoritePlaylistIds,
                playlistDto.getTitle(),
                playlistDto.getStyle()
        );

        return Result.success(new PageResult<>(playlistPage.getTotal(), playlistPage.getRecords()));
    }

    @Override

    public Result collectPlaylist(Long playlistId) {
        Map<String, Object> map = CurrentUserUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("type", 1).eq("playlist_id", playlistId);
        if (userFavoriteMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(MessageConstant.ADD + MessageConstant.FAILED);
        }

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setUserId(userId);
        userFavorite.setType(1);
        userFavorite.setPlaylistId(playlistId);
        userFavorite.setCreateTime(LocalDateTime.now());
        userFavoriteMapper.insert(userFavorite);

        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS, null);
    }

    @Override

    public Result cancelCollectPlaylist(Long playlistId) {
        Map<String, Object> map = CurrentUserUtil.get();
        Object userIdObj = map.get(JwtClaimsConstant.USER_ID);
        Long userId = TypeConversionUtil.toLong(userIdObj);

        QueryWrapper<UserFavorite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId).eq("type", 1).eq("playlist_id", playlistId);
        if (userFavoriteMapper.delete(queryWrapper) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }

}
