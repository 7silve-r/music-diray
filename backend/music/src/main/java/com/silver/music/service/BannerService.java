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

    Result addBanner(String bannerUrl);

    Result updateBanner(Long bannerId, String bannerUrl);

    Result updateBannerStatus(Long bannerId, Integer bannerStatus);

    Result deleteBanner(Long bannerId);

    Result deleteBanners(List<Long> bannerIds);

    Result<List<BannerVO>> getBannerList();
}
