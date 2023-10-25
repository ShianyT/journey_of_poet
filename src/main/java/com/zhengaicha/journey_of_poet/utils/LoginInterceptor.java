package com.zhengaicha.journey_of_poet.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if(request.getServletPath().contains(".")){
            return true;
        }

        // 1.判断是否需要拦截（ThreadLocal中是否有用户）
        if (UserHolder.getUser() == null) {
            // 没有，需要拦截，设置状态码
            response.setStatus(401);
            log.warn("拦截未知用户");
            // 拦截
            return false;
        }
        // 有用户，则放行
        return true;
    }
}
