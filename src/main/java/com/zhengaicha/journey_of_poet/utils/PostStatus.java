package com.zhengaicha.journey_of_poet.utils;

import lombok.Getter;

@Getter
public enum PostStatus {
    LIKE(1,"点赞"),
    UNLIKE(0,"用户取消点赞");

    private Integer code;
    private String msg;

    PostStatus(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
