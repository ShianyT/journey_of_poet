package com.zhengaicha.sycamore_street.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.sycamore_street.mapper.UserMapper;
import com.zhengaicha.sycamore_street.entity.User;
import com.zhengaicha.sycamore_street.service.UserService;
import org.springframework.stereotype.Service;

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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
}
