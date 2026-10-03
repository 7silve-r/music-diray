package com.silver.music.controller;

import com.silver.music.dto.BannerDto;
import com.silver.music.entity.Banner;
import com.silver.music.vo.BannerVO;
import com.silver.diary.common.PageResult;
import com.silver.diary.common.Result;
import com.silver.music.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
public class BannerController {

    @Autowired
    private BannerService bannerService;

    @PostMapping("/music/admin/getAllBanners")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<Banner>> getAllBanners(@RequestBody BannerDto bannerDto) {
        return bannerService.getAllBanners(bannerDto);
    }

    @PatchMapping("/music/admin/updateBannerStatus/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result updateBannerStatus(@PathVariable("id") Long bannerId, @RequestParam("status") Integer bannerStatus) {
        return bannerService.updateBannerStatus(bannerId, bannerStatus);
    }

    @DeleteMapping("/music/admin/deleteBanner/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteBanner(@PathVariable("id") Long bannerId) {
        return bannerService.deleteBanner(bannerId);
    }

    @DeleteMapping("/music/admin/deleteBanners")
    @PreAuthorize("hasRole('ADMIN')")
    public Result deleteBanners(@RequestBody List<Long> bannerIds) {
        return bannerService.deleteBanners(bannerIds);
    }

    @GetMapping("/music/public/banner/getBannerList")
    public Result<List<BannerVO>> getBannerList() {
        return bannerService.getBannerList();
    }
}
