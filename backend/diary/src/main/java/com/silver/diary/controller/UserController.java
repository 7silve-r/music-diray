package com.silver.diary.controller;

import com.silver.diary.common.Result;
import com.silver.diary.dto.LoginDto;
import com.silver.diary.dto.RegisterDto;
import com.silver.diary.dto.UserProfileUpdateDto;
import com.silver.diary.dto.UserPwdUpdateDto;
import com.silver.diary.entity.User;
import com.silver.diary.exception.BusinessException;
import com.silver.diary.service.UserService;
import com.silver.diary.upload.UploadResult;
import com.silver.diary.upload.UploadService;
import com.silver.diary.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
public class UserController {
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UploadService uploadService;

    /**
     * 注册
     */
    @PostMapping("/api/reg")
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

    /**
     *  登录
     */
    @PostMapping("/api/login")
    public Result<String> login(@RequestBody LoginDto dto) {
        User user = userService.lambdaQuery()
                .eq(User::getUsername, dto.getUsername())
                .one();
        if (user == null ||
            dto.getPassword() == null ||
            !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return Result.success(jwtUtil.generateToken(dto.getUsername()));
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/my/userinfo")
    public Result<User> getUserInfo(@RequestHeader("Authorization") String token) {
        String username = jwtUtil.getUsername(token);
        User user = userService.lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "账号不存在，请重新登录");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新资料
     */
    @PatchMapping("/my/profile")
    public Result<Void> updateProfile(@RequestHeader("Authorization") String token,
                                      @RequestBody UserProfileUpdateDto dto) {
        String username = jwtUtil.getUsername(token);
        User user = userService.lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "账号不存在，请重新登录");
        }
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        if (!userService.updateById(user)) {
            throw new BusinessException("操作未完成，数据可能以变化，请刷新后重试");
        }
        return Result.success();
    }

    /**
     * 更新密码
     */
    @PatchMapping("/my/password")
    public Result<Void> updatePassword(@RequestHeader("Authorization") String token,
                                       @RequestBody UserPwdUpdateDto dto) {
        String username = jwtUtil.getUsername(token);
        User user = userService.lambdaQuery().eq(User::getUsername, username).one();
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "账号不存在，请重新登录");
        }
        if (dto.getOldPwd() == null ||
            !passwordEncoder.matches(dto.getOldPwd(), user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        if (dto.getNewPwd() == null ||
            dto.getNewPwd().length() < 8 ||
            dto.getNewPwd().length() > 64) {
            throw new BusinessException("新密码需8到64位");
        }
        if (!dto.getReNewPwd().equals(dto.getNewPwd())) {
            throw new BusinessException("两次密码不一致");
        }
        user.setPassword(dto.getNewPwd());
        if (!userService.updateById(user)) {
            throw new BusinessException("操作未完成，数据可能已变化，请刷新后重试");
        }
        return Result.success();
    }

    /**
     * 更新头像
     */
    @PatchMapping(value = "/my/avator", consumes = "multipart/form-data")
    public Result<UploadResult> updateAvatar(@RequestHeader("Authorization") String token,
                                             @RequestParam("file") MultipartFile file) throws IOException {
        return Result.success(uploadService.avatar(jwtUtil.getUsername(token), file));
    }
}
