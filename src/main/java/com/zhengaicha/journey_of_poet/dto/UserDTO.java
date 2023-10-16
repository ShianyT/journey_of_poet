package com.zhengaicha.journey_of_poet.dto;

import lombok.Data;

/**
 * 用于数据响应，隐藏敏感信息
 */
@Data
public class UserDTO {
    private Integer uid;
    private String nickname;
    private String icon;
}
