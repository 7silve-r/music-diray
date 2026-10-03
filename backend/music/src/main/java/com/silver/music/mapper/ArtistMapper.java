package com.silver.music.mapper;

import com.silver.music.entity.Artist;
import com.silver.music.vo.ArtistDetailVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArtistMapper extends BaseMapper<Artist> {

    ArtistDetailVO getArtistDetailById(Long artistId);

}
