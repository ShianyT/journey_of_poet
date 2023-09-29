package com.zhengaicha.sycamore_street.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhengaicha.sycamore_street.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
