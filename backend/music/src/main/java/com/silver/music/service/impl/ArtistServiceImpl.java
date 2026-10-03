package com.silver.music.service.impl;

import org.springframework.transaction.annotation.Transactional;

import com.silver.diary.exception.BusinessException;

import com.silver.music.constant.JwtClaimsConstant;
import com.silver.music.constant.MessageConstant;
import com.silver.music.enumeration.LikeStatusEnum;
import com.silver.music.enumeration.RoleEnum;
import com.silver.music.mapper.ArtistMapper;
import com.silver.music.mapper.UserFavoriteMapper;
import com.silver.music.dto.ArtistAddDto;
import com.silver.music.dto.ArtistDto;
import com.silver.music.dto.ArtistUpdateDto;
import com.silver.music.entity.Artist;
import com.silver.music.entity.UserFavorite;
import com.silver.music.vo.ArtistDetailVO;
import com.silver.music.vo.ArtistNameVO;
import com.silver.music.vo.ArtistVO;
import com.silver.music.vo.SongVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.ArtistService;
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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service

public class ArtistServiceImpl extends ServiceImpl<ArtistMapper, Artist> implements ArtistService {

    @Autowired
    private ArtistMapper artistMapper;
    @Autowired
    private UserFavoriteMapper userFavoriteMapper;
    @Autowired
    private MinioService minioService;

    @Override
    public Result<PageResult<ArtistVO>> getAllArtists(ArtistDto artistDto) {

        Page<Artist> page = new Page<>(artistDto.getPageNum(), artistDto.getPageSize());
        QueryWrapper<Artist> queryWrapper = new QueryWrapper<>();

        if (artistDto.getArtistName() != null) {
            queryWrapper.like("name", artistDto.getArtistName());
        }
        if (artistDto.getGender() != null) {
            queryWrapper.eq("gender", artistDto.getGender());
        }
        if (artistDto.getArea() != null) {
            queryWrapper.like("area", artistDto.getArea());
        }

        IPage<Artist> artistPage = artistMapper.selectPage(page, queryWrapper);
        if (artistPage.getRecords().size() == 0) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, new PageResult<>(0L, null));
        }

        List<ArtistVO> artistVOList = artistPage.getRecords().stream()
                .map(artist -> {
                    ArtistVO artistVO = new ArtistVO();
                    BeanUtils.copyProperties(artist, artistVO);
                    return artistVO;
                }).toList();

        return Result.success(new PageResult<>(artistPage.getTotal(), artistVOList));
    }

    @Override
    public Result<PageResult<Artist>> getAllArtistsAndDetail(ArtistDto artistDto) {

        Page<Artist> page = new Page<>(artistDto.getPageNum(), artistDto.getPageSize());
        QueryWrapper<Artist> queryWrapper = new QueryWrapper<>();

        if (artistDto.getArtistName() != null) {
            queryWrapper.like("name", artistDto.getArtistName());
        }
        if (artistDto.getGender() != null) {
            queryWrapper.eq("gender", artistDto.getGender());
        }
        if (artistDto.getArea() != null) {
            queryWrapper.like("area", artistDto.getArea());
        }

        queryWrapper.orderByDesc("id");

        IPage<Artist> artistPage = artistMapper.selectPage(page, queryWrapper);
        if (artistPage.getRecords().size() == 0) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, new PageResult<>(0L, null));
        }

        return Result.success(new PageResult<>(artistPage.getTotal(), artistPage.getRecords()));
    }

    @Override
    public Result<List<ArtistNameVO>> getAllArtistNames() {
        List<Artist> artists = artistMapper.selectList(new QueryWrapper<Artist>().orderByDesc("id"));
        if (artists.isEmpty()) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, null);
        }

        List<ArtistNameVO> artistNameVOList = artists.stream()
                .map(artist -> {
                    ArtistNameVO artistNameVO = new ArtistNameVO();
                    artistNameVO.setArtistId(artist.getArtistId());
                    artistNameVO.setArtistName(artist.getArtistName());
                    return artistNameVO;
                }).toList();

        return Result.success(artistNameVOList);
    }

    @Override
    public Result<List<ArtistVO>> getRandomArtists() {
        QueryWrapper<Artist> queryWrapper = new QueryWrapper<>();
        queryWrapper.last("ORDER BY RAND() LIMIT 10");

        List<Artist> artists = artistMapper.selectList(queryWrapper);
        if (artists.isEmpty()) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, null);
        }

        List<ArtistVO> artistVOList = artists.stream()
                .map(artist -> {
                    ArtistVO artistVO = new ArtistVO();
                    BeanUtils.copyProperties(artist, artistVO);
                    return artistVO;
                }).toList();

        return Result.success(artistVOList);
    }

    @Override
    public Result<ArtistDetailVO> getArtistDetail(Long artistId, HttpServletRequest request) {
        ArtistDetailVO artistDetailVO = artistMapper.getArtistDetailById(artistId);
        if (artistDetailVO == null) throw new BusinessException(404, "歌手不存在");

        List<SongVO> songVOList = artistDetailVO.getSongs();
        songVOList.forEach(songVO -> songVO.setLikeStatus(LikeStatusEnum.DEFAULT.getId()));

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

        artistDetailVO.setSongs(songVOList);

        return Result.success(artistDetailVO);
    }

    @Override
    public Result<Long> getAllArtistsCount(Integer gender, String area) {
        QueryWrapper<Artist> queryWrapper = new QueryWrapper<>();
        if (gender != null) {
            queryWrapper.eq("gender", gender);
        }
        if (area != null) {
            queryWrapper.eq("area", area);
        }

        return Result.success(artistMapper.selectCount(queryWrapper));
    }

    @Override
    @Transactional
    public Result<Void> addArtist(ArtistAddDto artistAddDto) {
        QueryWrapper<Artist> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", artistAddDto.getArtistName());
        if (artistMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(MessageConstant.ARTIST + MessageConstant.ALREADY_EXISTS);
        }

        Artist artist = new Artist();
        BeanUtils.copyProperties(artistAddDto, artist);
        artistMapper.insert(artist);

        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS, null);
    }

    @Override
    @Transactional
    public Result<Void> updateArtist(ArtistUpdateDto artistUpdateDto) {
        Long artistId = artistUpdateDto.getArtistId();

        Artist artistByArtistName = artistMapper.selectOne(new QueryWrapper<Artist>().eq("name", artistUpdateDto.getArtistName()));
        if (artistByArtistName != null && !artistByArtistName.getArtistId().equals(artistId)) {
            throw new BusinessException(MessageConstant.ARTIST + MessageConstant.ALREADY_EXISTS);
        }

        Artist artist = new Artist();
        BeanUtils.copyProperties(artistUpdateDto, artist);
        if (artistMapper.updateById(artist) == 0) {
            throw new BusinessException(MessageConstant.UPDATE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS, null);
    }

    @Override
    @Transactional
    public Result<Void> updateArtistAvatar(Long artistId, String avatar) {
        Artist artist = artistMapper.selectById(artistId);
        if (artist == null) throw new BusinessException(404, "资源不存在");
        String avatarUrl = artist.getAvatar();

        artist.setAvatar(avatar);
        if (artistMapper.updateById(artist) == 0) {
            throw new BusinessException(MessageConstant.UPDATE + MessageConstant.FAILED);
        }

        com.silver.music.upload.UploadCleanup.afterCommit(minioService, avatarUrl);
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS, null);
    }

    @Override
    @Transactional
    public Result<Void> deleteArtist(Long artistId) {

        Artist artist = artistMapper.selectById(artistId);
        if (artist == null) {
            throw new BusinessException(MessageConstant.ARTIST + MessageConstant.NOT_FOUND);
        }
        String avatarUrl = artist.getAvatar();

        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            com.silver.music.upload.UploadCleanup.afterCommit(minioService, avatarUrl);
        }

        if (artistMapper.deleteById(artistId) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }

    @Override
    @Transactional
    public Result<Void> deleteArtists(List<Long> artistIds) {

        List<Artist> artists = artistMapper.selectByIds(artistIds);
        List<String> avatarUrlList = artists.stream()
                .map(Artist::getAvatar)
                .filter(avatarUrl -> avatarUrl != null && !avatarUrl.isEmpty())
                .toList();

        for (String avatarUrl : avatarUrlList) {
            com.silver.music.upload.UploadCleanup.afterCommit(minioService, avatarUrl);
        }

        if (artistMapper.deleteByIds(artistIds) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }

        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }

}
