package com.silver.music.service;

import com.silver.music.dto.PlaylistDto;
import com.silver.music.dto.SongDto;
import com.silver.music.entity.UserFavorite;
import com.silver.music.vo.PlaylistVO;
import com.silver.music.vo.SongVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.baomidou.mybatisplus.spring.service.IService;

public interface UserFavoriteService extends IService<UserFavorite> {

    Result<PageResult<SongVO>> getUserFavoriteSongs(SongDto songDto);

    Result collectSong(Long songId);

    Result cancelCollectSong(Long songId);

    Result<PageResult<PlaylistVO>> getUserFavoritePlaylists(PlaylistDto playlistDto);

    Result collectPlaylist(Long playlistId);

    Result cancelCollectPlaylist(Long playlistId);

}
