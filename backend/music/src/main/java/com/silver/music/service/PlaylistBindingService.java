package com.silver.music.service;

import com.silver.music.entity.PlaylistBinding;
import com.baomidou.mybatisplus.spring.service.IService;

public interface PlaylistBindingService extends IService<PlaylistBinding> {

    void update(Long id, java.util.List<Long> songIds);
}
