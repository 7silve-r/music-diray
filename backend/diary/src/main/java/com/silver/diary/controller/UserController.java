package com.silver.diary.controller;

import com.silver.diary.common.Result;
import com.silver.diary.dto.RegisterDto;
import com.silver.diary.entity.User;
import com.silver.diary.exception.BusinessException;
import com.silver.diary.service.UserService;
import com.silver.diary.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 注册
     */
    @PostMapping("api/reg")
    public Result<Void> register(@RequestBody RegisterDto dto) {
        if (dto.getUsername() == null
                || !dto.getUsername().matches("[a-zA-Z0-9_]{5,30}")
                || dto.getPassword() == null
                || dto.getPassword().length() < 8
                || dto.getPassword().length() > 64)
            throw new BusinessException("用户名需5到30位字母/数字/下划线，密码需8到64位");
        if (userService.lambdaQuery().eq(User::getUsername, dto.getUsername()).exists()){
            throw new BusinessException("用户名已存在");
        }
        if(!dto.getPassword().equals(dto.getRePassword())){
            throw new BusinessException("两次密码不一致");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (!userService.save(user)) {
            throw new BusinessException("操作未完成，数据可能已变化，请刷新后重试");
        }
        return Result.success();
    }
}
