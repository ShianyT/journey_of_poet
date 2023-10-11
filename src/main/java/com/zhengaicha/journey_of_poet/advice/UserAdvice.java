package com.zhengaicha.journey_of_poet.advice;

import com.zhengaicha.journey_of_poet.common.Result;
import com.zhengaicha.journey_of_poet.entity.User;
import jakarta.annotation.Resource;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@Aspect
public class UserAdvice {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Pointcut("execution(* com.zhengaicha.journey_of_poet.controller.UserController.signUp(..))")
    public void signUpPoint(){}
    @Pointcut("execution(* com.zhengaicha.journey_of_poet.controller.UserController.verifyCode(*))")
    public void verifyCodePoint(){}
    @Pointcut("execution(* com.zhengaicha.journey_of_poet.controller.UserController.modifyPasswordByEmail(..))")
    public void modifyPasswordByEmailPoint(){}
    @Pointcut("execution(* com.zhengaicha.journey_of_poet.controller.UserController.modifyMail(..))")
    public void modifyMailPoint(){}

    /**
     * 用于UserController方法中的验证码验证，让Controller只需要写验证码验证过后的方法
     */
    @Around(value = "signUpPoint() || verifyCodePoint() || modifyPasswordByEmailPoint() || modifyMailPoint() ")
    public Result<User> sendCodeAdvice(ProceedingJoinPoint pjp) throws Throwable {
        User user = (User) pjp.getArgs()[0];
        String mailCode = stringRedisTemplate.opsForValue().get(user.getMail());
        // 1、检查键值对是否存在
        if (mailCode != null) {
            // 2、比对验证码
            if (mailCode.equals(user.getCode())) {
                // 3、删除键值对
                stringRedisTemplate.delete(user.getMail());
                Result<User> result = (Result<User>) pjp.proceed();
                return result;
            } else return Result.error("验证码错误");
        } else return Result.error("验证码已失效");
    }
}
