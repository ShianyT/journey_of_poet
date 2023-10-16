package com.zhengaicha.journey_of_poet.utils;

import com.zhengaicha.journey_of_poet.dto.UserDTO;

public class UserHolder {
    private static final ThreadLocal<UserDTO> THREAD_LOCAL = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        THREAD_LOCAL.set(user);
    }

    public static UserDTO getUser(){
        return THREAD_LOCAL.get();
    }

    public static void removeUser(){
        THREAD_LOCAL.remove();
    }
}
