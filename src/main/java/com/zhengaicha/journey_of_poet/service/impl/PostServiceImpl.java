package com.zhengaicha.journey_of_poet.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.Post;
import com.zhengaicha.journey_of_poet.mapper.PostMapper;
import com.zhengaicha.journey_of_poet.service.PostService;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.apache.commons.io.FileUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static com.zhengaicha.journey_of_poet.utils.RedisConstants.POST_IMAGES_KEY_PREFIX;
import static com.zhengaicha.journey_of_poet.utils.SystemConstants.POST_IMAGES_PATH;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result getPost(Integer currentPage) {
        if (!Objects.isNull(UserHolder.getUser())) {
            Integer uid = UserHolder.getUser().getUid();
            IPage<Post> postPage = lambdaQuery().eq(Post::getUid, uid).page(new Page<>(currentPage, 10));
            List<Post> posts = postPage.getRecords();
            if (!posts.isEmpty()) {
                return Result.success(posts);
            } else return Result.error("帖子不存在");
        }
        return Result.error("出错啦！请先登录");
    }

    @Override
    public Result savePost(Post post) {
        UserDTO user = UserHolder.getUser();
        if (!Objects.isNull(user)) {
            // 将图片存入post中
            String imageKey = POST_IMAGES_KEY_PREFIX + user.getUid();
            Long size = stringRedisTemplate.opsForHash().size(imageKey);
            if (size >= 0) {
                List<Object> imageList = stringRedisTemplate.opsForHash().values(imageKey);
                String images = imageList.toString().replace("[", "").replace("]", "");
                post.setImages(images);
                // 清空数据
                stringRedisTemplate.delete(imageKey);
            }
            post.setUid(user.getUid());
            post.setNickname(user.getNickname());
            post.setIcon(user.getIcon());
            this.save(post);
            return Result.success();
        }
        return Result.error("出错啦！请先登录");
    }


    @Override
    public Result uploadImage(MultipartFile multipartFile) {
        // 用户是否登录
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user))
            return Result.error("出错啦！请先登录");

        // 是否超过图片上传上限
        String imageKey = POST_IMAGES_KEY_PREFIX + user.getUid();
        long size = stringRedisTemplate.opsForHash().size(imageKey);
        if (size >= 4)
            return Result.error("图片上传已达上限");

        // 是否上传有效
        if (Objects.isNull(multipartFile))
            return Result.error("请先上传图片");

        // 是否为图片文件
        String originalFilename = multipartFile.getOriginalFilename();
        String format = originalFilename.substring(originalFilename.lastIndexOf("."));
        if (!(format.equals(".jpg") || format.equals(".jpeg") || format.equals(".png")))
            return Result.error("请上传jpg/jpeg/png格式的图片文件");

        // 图片不可超过5M
        if (multipartFile.getSize() >= (1024 * 1024 * 5L))
            return Result.error("图片不可超过5M");

        // 生成文件名
        String fileName = UUID.randomUUID().toString().replace("-", "")
                + RandomUtil.randomNumbers(5) + format;
        File file = new File(POST_IMAGES_PATH + "/" + fileName);
        try {
            // multipartFile.transferTo(file);
            FileUtils.copyInputStreamToFile(multipartFile.getInputStream(),file);
            file.deleteOnExit();
            stringRedisTemplate.opsForHash().put(imageKey, size+"" , fileName);
            // 图片回显
            return Result.success(stringRedisTemplate.opsForHash().values(imageKey));
        } catch (IOException e) {
            log.error("服务器存储文件失败" + e.getMessage());
            return Result.error("图片文件存储失败，服务器异常");
        }
    }


    public Result deleteImage(int index) {
        // 判断用户是否为空
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user))
            return Result.error("出错啦！请先登录");

        // 判断是否超过角标
        String imageKey = POST_IMAGES_KEY_PREFIX + user.getUid();
        Long size = stringRedisTemplate.opsForHash().size(imageKey);
        if (index >= size)
            return Result.error("超过角标范围");

        // 删除该角标下的图片
        stringRedisTemplate.opsForHash().delete(imageKey, index+"");
        // 图片回显
        return Result.success(stringRedisTemplate.opsForHash().values(imageKey));
    }

    @Override
    public Result cancelPost() {
        UserDTO user = UserHolder.getUser();
        // 判断用户是否为空
        if (!Objects.isNull(user)) {
            String imageKey = POST_IMAGES_KEY_PREFIX + user.getUid();
            stringRedisTemplate.delete(imageKey);
            return Result.success();
        } else return Result.error("出错啦！请先登录");
    }
}
