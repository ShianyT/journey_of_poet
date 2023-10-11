package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.mapper.UserMapper;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:UserServiceImpl
 * Package:com.zhengaicha.sycamore_street.service.impl
 * Description:
 *
 * @Author: 温书彦
 * @Create:2023/9/30 - 0:16
 * @Version: v1.0
 */
@Service
@Component
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Resource
    private JavaMailSender sender;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Value("${spring.mail.username}")
    private String from;
    @Override
    public boolean sendCode(String mail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mail);
            message.setFrom(from);
            message.setSubject("[诗旅]邮箱验证码");
            Random random = new Random();
            String code = random.nextInt(899999) + 100000 + "";//保持在六位数
            stringRedisTemplate.opsForValue().set(mail,code,60 * 3L, TimeUnit.SECONDS);
            message.setText("尊敬的用户您好！\n您的验证码是：" + code + "，请在3分钟内进行验证。如果该验证码不为您本人申请，请无视。");
            sender.send(message);
            return true;
        } catch (MailException e) {
            log.error("邮件发送失败");
            return false;
        }
    }
}
