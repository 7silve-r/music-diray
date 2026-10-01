package com.silver.diary.dto;

import lombok.Data;

@Data
public class UserPwdUpdateDto {
    private String oldPwd;
    private String newPwd;
    private String reNewPwd;
}
