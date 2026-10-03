package com.silver.music.service.impl;

import com.silver.music.entity.Style;
import com.silver.music.mapper.StyleMapper;
import com.silver.music.service.StyleService;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class StyleServiceImpl extends ServiceImpl<StyleMapper, Style> implements StyleService {

}
