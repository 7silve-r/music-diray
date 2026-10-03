package com.silver.music.dto;

import com.silver.music.enumeration.BannerStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BannerDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @jakarta.validation.constraints.Min(1)
    private Integer pageNum = 1;

    @NotNull
    @jakarta.validation.constraints.Min(1)
    @jakarta.validation.constraints.Max(100)
    private Integer pageSize = 10;

    private BannerStatusEnum bannerStatus;

}
