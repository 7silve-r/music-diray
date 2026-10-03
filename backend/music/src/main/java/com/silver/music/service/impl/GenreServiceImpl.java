package com.silver.music.service.impl;

import com.silver.music.entity.Genre;
import com.silver.music.mapper.GenreMapper;
import com.silver.music.service.GenreService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class GenreServiceImpl extends ServiceImpl<GenreMapper, Genre> implements GenreService {

}
