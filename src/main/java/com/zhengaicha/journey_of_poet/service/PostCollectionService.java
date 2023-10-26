package com.zhengaicha.journey_of_poet.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.PostCollection;

import java.util.List;

public interface PostCollectionService extends IService<PostCollection> {
    public Result collection(PostCollection postCollection);

    /**
     * 增加收藏
     */
    public Result addCollection(PostCollection postLike);

    /**
     * 减少收藏
     */
    public Result deleteCollection(PostCollection postLike);

    /**
     * 将收藏数据存入mysql
     */
    public void saveCollectionFromRedis();

    /**
     * 将收藏量存入mysql
     */
    public void saveCollectionNumFromRedis();

    /**
     * 查询用户是否收藏
     */
    public Boolean isCollection(Integer postId, Integer uid);

    public List<PostCollection> getCollectedPostFromRedis(UserDTO user);

}
