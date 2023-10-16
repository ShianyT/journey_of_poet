package com.zhengaicha.journey_of_poet.dto;

import lombok.Data;

/**
 * 用于用户登陆注册
 */
@Data
public class LoginDTO {
    private String mail;
    private String password;
    private String code;
}
