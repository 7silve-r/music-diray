package com.silver.music.entity;

import com.silver.music.enumeration.BannerStatusEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.io.Serial;
import java.io.Serializable;

@Data


@TableName("tb_banner")
public class Banner implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long bannerId;

    @TableField("banner_url")
    private String bannerUrl;

    @TableField("status")
    private BannerStatusEnum bannerStatus;
}
