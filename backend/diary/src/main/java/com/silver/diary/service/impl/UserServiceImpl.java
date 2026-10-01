package com.silver.diary.service.impl;

import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.silver.diary.entity.User;
import com.silver.diary.mapper.UserMapper;
import com.silver.diary.service.UserService;

public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
}
