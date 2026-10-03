package com.silver.music.service;

import com.silver.music.dto.BannerDto;
import com.silver.music.entity.Banner;
import com.silver.music.vo.BannerVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.baomidou.mybatisplus.spring.service.IService;

import java.util.List;

public interface BannerService extends IService<Banner> {

    Result<PageResult<Banner>> getAllBanners(BannerDto bannerDto);

    Result<Void> addBanner(String bannerUrl);

    Result<Void> updateBanner(Long bannerId, String bannerUrl);

    Result<Void> updateBannerStatus(Long bannerId, Integer bannerStatus);

    Result<Void> deleteBanner(Long bannerId);

    Result<Void> deleteBanners(List<Long> bannerIds);

    Result<List<BannerVO>> getBannerList();
}
