package com.zhengaicha.journey_of_poet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.LoginDTO;
import com.zhengaicha.journey_of_poet.dto.Result;

import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.mapper.UserMapper;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import com.zhengaicha.journey_of_poet.service.UserService;
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

import static com.zhengaicha.journey_of_poet.utils.RedisConstants.LOGIN_CODE_KEY;
import static com.zhengaicha.journey_of_poet.utils.RedisConstants.LOGIN_CODE_TTL;
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
                stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + mail, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
                message.setText("尊敬的用户您好！\n您的验证码是：" + code + "，请在3分钟内进行验证。如果该验证码不为您本人申请，请无视。");
                // javaMailSender.send(message);
                log.warn(code);
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
            } while (lambdaQuery().eq(User::getUid, uid).exists());
            String nickName = USER_NICKNAME_PREFIX + RandomUtil.randomNumbers(12);
            // 4.创建新用户
            User newUser = new User(uid, login.getMail(), login.getPassword(), nickName, USER_DEFAULT_ICON_NAME);
            this.save(newUser);
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
        // 将User对象转为HashMap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap =
                BeanUtil.beanToMap(userDTO, new HashMap<>(), CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        // 存储
        String tokenKey = LOGIN_CODE_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 设置token有效期
        stringRedisTemplate.expire(tokenKey, USER_TOKEN_TTL, TimeUnit.DAYS);
        HashMap<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        // 返回token
        return Result.success(tokenMap);
    }

    /**
     * 更换新邮箱
     */
    public Result modifyMail(LoginDTO newMailUser, HttpServletRequest request) {
        UserDTO oldMailUser = UserHolder.getUser();
        if (lambdaQuery().eq(User::getMail, newMailUser.getMail()).exists())
            return Result.error("该邮箱账号已存在");

        boolean update = lambdaUpdate().eq(User::getUid, oldMailUser.getUid())
                .set(User::getMail, newMailUser.getMail()).update();
        if (update) {
            if (redisUtils.deleteToken(request))
                return Result.success();
            return Result.error("数据刷新失败");
        } else return Result.error("修改失败");
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

        // 获取文件名
        String OriginalFilename = multipartFile.getOriginalFilename();
        String format = OriginalFilename.substring(OriginalFilename.lastIndexOf("."));
        // 判断文件后缀是否为图片类型
        if (!(format.equals(".jpg") || format.equals(".jpeg") || format.equals(".png")))
            return Result.error("请上传jpg/jpeg/png格式的图片文件");

        if (multipartFile.getSize() > (1024 * 1024 * 5))
            return Result.error("图片不可超过5M");

        String fileName = UUID.randomUUID().toString().replace("-", "")
                + RandomUtil.randomNumbers(5) + format;
        File file = new File(ICON_PATH + "/" + fileName);
        try {
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);
            file.deleteOnExit();
        } catch (IOException e) {
            log.error("服务器存储文件失败" + e.getMessage());
            throw new RuntimeException("图片文件存储失败，服务器异常", e);
        }
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

    @Override
    public boolean isUserNotExist(Integer commentedUid) {
        return !lambdaQuery().eq(User::getUid,commentedUid).exists();
    }
}
