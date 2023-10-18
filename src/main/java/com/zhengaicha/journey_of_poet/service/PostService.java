package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.Post;
import org.springframework.web.multipart.MultipartFile;

public interface PostService extends IService<Post> {

    public Result getPost(Integer currentPage);

    public Result savePost(Post post);

    public Result uploadImage(MultipartFile multipartFile);

    public Result deleteImage(int index);

    public Result cancelPost();

    public Result deletePost(int currentPage, int index);
}
