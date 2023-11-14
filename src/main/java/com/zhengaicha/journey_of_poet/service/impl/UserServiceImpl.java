package com.zhengaicha.journey_of_poet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.dto.LoginDTO;
import com.zhengaicha.journey_of_poet.dto.Result;

import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.mapper.UserMapper;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import com.zhengaicha.journey_of_poet.service.UserService;
import com.zhengaicha.journey_of_poet.utils.ImageUtil;
import com.zhengaicha.journey_of_poet.utils.RedisUtils;
import com.zhengaicha.journey_of_poet.utils.RegexUtils;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.zhengaicha.journey_of_poet.utils.RedisConstants.*;
import static com.zhengaicha.journey_of_poet.utils.SystemConstants.*;

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
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisUtils redisUtils;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private UserInfoService userInfoService;


    /**
     * 发送验证码给用户
     */
    @Override
    public Result sendCode(String mail) {
        try {
            // 1.邮箱是否有效
            if (RegexUtils.isEmailValid(mail)) {
                // 2.编辑邮件
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(mail);
                message.setFrom(from);
                message.setSubject("[诗旅]邮箱验证码");
                String code = UUID.randomUUID().toString(true).substring(0, 6); // 保持在六位数
                // 3.将验证码存储在redis中
                stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY_PREFIX + mail, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
                message.setText("尊敬的用户您好！\n您的验证码是：" + code + "，请在3分钟内进行验证。如果该验证码不为您本人申请，请无视。");
                // javaMailSender.send(message);
                // log.warn(code);
                return Result.success(code);
            } else return Result.error("该邮箱无效");
        } catch (MailException e) {
            log.error("邮件发送失败");
            return Result.error("邮件发送失败");
        }
    }

    /**
     * 用户注册账号
     */
    @Override
    public Result createUser(LoginDTO login) {
        // 1.检查邮箱是否已经注册
        User user = lambdaQuery().eq(User::getMail, login.getMail()).one();
        if (user == null) {
            // 2、密码加密
            login.setPassword(DigestUtils.md5DigestAsHex(login.getPassword().getBytes()));
            int uid;
            // 3、获得非重复uid
            do {
                uid = UUID.randomUUID().toString(true).hashCode();
                uid = uid < 0 ? -uid : uid;
                uid = Integer.parseInt(String.format("%010d", uid).substring(0, 10));
            } while (uid <= 999999999 || lambdaQuery().eq(User::getUid, uid).exists());
            String nickName = USER_NICKNAME_PREFIX + RandomUtil.randomNumbers(12);
            // 4.创建新用户
            User newUser = new User(uid, login.getMail(), login.getPassword(), nickName, USER_DEFAULT_ICON_NAME);
            save(newUser);
            // 5、创建用户详细信息实例并保存
            userInfoService.init(newUser);
            return Result.success();
        } else return Result.error("账号已存在");
    }

    /**
     * 登录
     */
    @Override
    public Result login(LoginDTO loginUser) {
        // 校验邮箱是否有效
        String mail = loginUser.getMail();
        if (!RegexUtils.isEmailValid(mail))
            return Result.error("邮箱格式错误");

        // 判断用户是否存在
        User user = this.lambdaQuery().eq(User::getMail, mail).one();
        if (Objects.isNull(user))
            return Result.error("用户不存在");

        if (Objects.isNull(user.getPassword()))
            return Result.error("请输入密码");

        if (!user.getPassword().equals(DigestUtils.md5DigestAsHex(loginUser.getPassword().getBytes())))
            return Result.error("密码错误");

        // TODO 改用jwt存储用户信息生成token，redis保存token
        // 随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);

        // 将User对象转为json
        ObjectMapper objectMapper = new ObjectMapper();
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        String userDTOJson = null;
        try {
            userDTOJson = objectMapper.writeValueAsString(userDTO);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        // 存储并设置过期时间
        stringRedisTemplate.opsForValue().set(redisUtils.getTokenKey(token),userDTOJson,USER_TOKEN_TTL,TimeUnit.DAYS);
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        // 返回token
        return Result.success(tokenMap);
    }

    /**
     * 当用户忘记密码时修改密码，通过邮箱发送验证码来修改
     */
    public Result modifyPasswordByEmail(LoginDTO user, HttpServletRequest request) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        boolean update = lambdaUpdate().eq(User::getMail, user.getMail())
                .set(User::getPassword, user.getPassword()).update();
        if (update) {
            if (redisUtils.deleteToken(request))
                return Result.success();
            return Result.error("数据刷新失败");
        } else return Result.error("修改失败");
    }

    /**
     * 当用户记得旧密码并想修改密码，通过旧密码来修改
     */
    public Result modifyPasswordByOldPassword(Map<String, String> passwords, HttpServletRequest request) {
        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");
        UserDTO user = UserHolder.getUser();
        User user1 = lambdaQuery().eq(User::getUid, user.getUid()).one();

        if (!user1.getPassword().equals(DigestUtils.md5DigestAsHex(oldPassword.getBytes())))
            return Result.error("旧密码错误");

        boolean update = this.lambdaUpdate().eq(User::getMail, user1.getMail())
                .set(User::getPassword, DigestUtils.md5DigestAsHex(newPassword.getBytes())).update();
        if (update) {
            if (redisUtils.deleteToken(request))
                return Result.success();
            return Result.error("数据刷新失败");
        } else return Result.error("密码修改失败");
    }


    /**
     * 用户上传头像
     */
    public Result uploadIcon(MultipartFile multipartFile, HttpServletRequest request) {
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user))
            return Result.error("出错啦！请登录");
        if (Objects.isNull(multipartFile))
            return Result.error("请先上传图片");

        // 判断图片格式以及大小
        String OriginalFilename = multipartFile.getOriginalFilename();
        if(!ImageUtil.isValidImage(OriginalFilename)){
            return Result.error("请上传正确的图片格式");
        }
        if (multipartFile.getSize() > (1024 * 1024 * 5))
            return Result.error("图片大小不可超过5M");

        // 保存原图片并生成压缩图
        String fileName = ImageUtil.saveImage(multipartFile,"icon");

        this.lambdaUpdate().eq(User::getUid, user.getUid()).set(User::getIcon, fileName).update();
        boolean isFlush = redisUtils.flushTokenData("icon", fileName, request);
        if (isFlush) return Result.success();
        else return Result.error("数据刷新失败");
    }

    /**
     * 修改昵称
     */
    public Result modifyNickname(String newNickname, HttpServletRequest request) {
        if (Objects.isNull(newNickname))
            return Result.error("请先输入昵称");

        UserDTO user = UserHolder.getUser();
        this.lambdaUpdate().eq(User::getUid, user.getUid()).set(User::getNickname, newNickname).update();
        if (redisUtils.flushTokenData("nickname", newNickname, request))
            return Result.success();
        else return Result.error("数据刷新失败");
    }

    /**
     * 修改性别
     */
    public Result modifyGender(Integer gender) {
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return Result.error("出错啦！请登录");
        }

        if (Objects.isNull(gender)) {
            return Result.error("请先选择选项");
        }

        boolean update = this.lambdaUpdate().eq(User::getUid, user.getUid())
                .set(User::getGender, gender).update();
        if (update) {
            return Result.success();
        }
        return Result.error("修改失败");
    }


    /**
     * 修改个性签名
     */
    public Result modifySignature(String signature) {

        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return Result.error("出错啦！请登录");
        }

        boolean update = this.lambdaUpdate().eq(User::getUid, user.getUid())
                .set(User::getSignature, signature).update();
        if (update) {
            return Result.success();
        }
        return Result.error("修改失败");

    }

    /**
     * 用于主页用户信息展示
     */
    @Override
    public Result showUser() {
        UserDTO user = UserHolder.getUser();
        User userInfo = lambdaQuery().eq(User::getUid, user.getUid()).one();
        userInfo.setMail(null);
        userInfo.setPassword(null);
        return Result.success(userInfo);
    }

    /**
     * 通过uid获得用户
     */
    @Override
    public User getOne(Integer uid) {
        return lambdaQuery().eq(User::getUid, uid).one();
    }
}
