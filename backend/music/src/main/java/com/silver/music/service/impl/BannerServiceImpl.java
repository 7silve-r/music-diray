package com.silver.music.service.impl;

import com.silver.diary.exception.BusinessException;

import com.silver.music.constant.MessageConstant;
import com.silver.music.enumeration.BannerStatusEnum;
import com.silver.music.mapper.BannerMapper;
import com.silver.music.dto.BannerDto;
import com.silver.music.entity.Banner;
import com.silver.music.vo.BannerVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.BannerService;
import com.silver.music.service.MinioService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Autowired
    private BannerMapper bannerMapper;
    @Autowired
    private MinioService minioService;

    @Override

    public Result<PageResult<Banner>> getAllBanners(BannerDto bannerDto) {

        Page<Banner> page = new Page<>(bannerDto.getPageNum(), bannerDto.getPageSize());
        QueryWrapper<Banner> queryWrapper = new QueryWrapper<>();
        if (bannerDto.getBannerStatus() != null) {
            queryWrapper.eq("status", bannerDto.getBannerStatus().getId());
        }

        queryWrapper.orderByDesc("id");

        IPage<Banner> bannerPage = bannerMapper.selectPage(page, queryWrapper);
        if (bannerPage.getRecords().size() == 0) {
            return Result.success(MessageConstant.DATA_NOT_FOUND, new PageResult<>(0L, null));
        }

        return Result.success(new PageResult<>(bannerPage.getTotal(), bannerPage.getRecords()));
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> addBanner(String bannerUrl) {
        Banner banner = new Banner();
        banner.setBannerUrl(bannerUrl);
        banner.setBannerStatus(BannerStatusEnum.ENABLE);

        if (bannerMapper.insert(banner) == 0) {
            throw new BusinessException(MessageConstant.ADD + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.ADD + MessageConstant.SUCCESS, null);
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> updateBanner(Long bannerId, String bannerUrl) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) throw new BusinessException(404, "资源不存在");
        String oldBannerUrl = banner.getBannerUrl();

        banner.setBannerUrl(bannerUrl);
        if (bannerMapper.updateById(banner) == 0) {
            throw new BusinessException(MessageConstant.UPDATE + MessageConstant.FAILED);
        }

        com.silver.music.upload.UploadCleanup.afterCommit(minioService, oldBannerUrl);
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS, null);
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> updateBannerStatus(Long bannerId, Integer bannerStatus) {

        BannerStatusEnum statusEnum;
        if (bannerStatus == 0) {
            statusEnum = BannerStatusEnum.ENABLE;
        } else if (bannerStatus == 1) {
            statusEnum = BannerStatusEnum.DISABLE;
        } else {
            throw new BusinessException(MessageConstant.BANNER_STATUS_INVALID);
        }

        Banner banner = new Banner();
        banner.setBannerId(bannerId);
        banner.setBannerStatus(statusEnum);

        if (bannerMapper.updateById(banner) == 0) {
            throw new BusinessException(MessageConstant.UPDATE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.UPDATE + MessageConstant.SUCCESS, null);

    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> deleteBanner(Long bannerId) {
        Banner banner = bannerMapper.selectById(bannerId);
        if (banner == null) {
            throw new BusinessException(MessageConstant.DATA_NOT_FOUND);
        }
        String bannerUrl = banner.getBannerUrl();
        if (bannerUrl != null && !bannerUrl.isEmpty()) {
            com.silver.music.upload.UploadCleanup.afterCommit(minioService, bannerUrl);
        }

        if (bannerMapper.deleteById(bannerId) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }

    @Override

    @org.springframework.transaction.annotation.Transactional
    public Result<Void> deleteBanners(List<Long> bannerIds) {
        List<Banner> banners = bannerMapper.selectByIds(bannerIds);
        List<String> bannerUrlList = banners.stream()
                .map(Banner::getBannerUrl)
                .filter(url -> url != null && !url.isEmpty())
                .toList();
        bannerUrlList.forEach(url -> minioService.deleteFile(url));

        if (bannerMapper.deleteByIds(bannerIds) == 0) {
            throw new BusinessException(MessageConstant.DELETE + MessageConstant.FAILED);
        }
        return Result.success(MessageConstant.DELETE + MessageConstant.SUCCESS, null);
    }

    @Override

    public Result<List<BannerVO>> getBannerList() {

        List<Banner> banners = bannerMapper.selectList(new QueryWrapper<Banner>()
                .eq("status", BannerStatusEnum.ENABLE.getId())
                .orderByDesc("id")
                .last("limit 9"));

        List<BannerVO> bannerVOList = banners.stream()
                .map(banner -> {
                    BannerVO bannerVO = new BannerVO();
                    BeanUtils.copyProperties(banner, bannerVO);
                    return bannerVO;
                }).toList();

        return Result.success(bannerVOList);
    }

}
