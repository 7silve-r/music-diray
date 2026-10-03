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
    private Integer pageNum;

    @NotNull
    private Integer pageSize;

    private BannerStatusEnum bannerStatus;

}
