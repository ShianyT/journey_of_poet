package com.zhengaicha.journey_of_poet.constants;

import lombok.Getter;

@Getter
public enum PostStatus {
    LIKE(1,"点赞"),
    UNLIKE(0,"取消点赞"),
    COLLECTION(1,"收藏"),
    UNCOLLECTION(0,"取消收藏");

    private final Integer code;
    private final String msg;

    PostStatus(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
