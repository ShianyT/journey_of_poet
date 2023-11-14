package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.Post;
import com.zhengaicha.journey_of_poet.entity.PostCollection;
import com.zhengaicha.journey_of_poet.mapper.PostCollectionMapper;
import com.zhengaicha.journey_of_poet.service.PostCollectionService;
import com.zhengaicha.journey_of_poet.service.PostService;
import com.zhengaicha.journey_of_poet.constants.PostStatus;
import com.zhengaicha.journey_of_poet.utils.RedisConstants;
import com.zhengaicha.journey_of_poet.utils.RedisUtils;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.zhengaicha.journey_of_poet.constants.PostStatus.COLLECTION;
import static com.zhengaicha.journey_of_poet.constants.PostStatus.LIKE;

@Service
public class PostCollectionServiceImpl extends ServiceImpl<PostCollectionMapper, PostCollection>
        implements PostCollectionService {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private PostService postService;

    @Override
    public Result collection(PostCollection postCollection) {
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return Result.error("出错啦！请登录");
        }
        postCollection.setUid(user.getUid());
        // 增加收藏
        if (LIKE.getCode().equals(postCollection.getStatus())) {
            return addCollection(postCollection);
        }
        // 减少收藏
        if (PostStatus.UNLIKE.getCode().equals(postCollection.getStatus())) {
            return deleteCollection(postCollection);
        }
        return Result.error("传参错误");
    }

    public Result addCollection(PostCollection postCollection) {
        // 在redis中查询是否有该条收藏记录
        String collectionDetail = redisUtils.getCollectionDetail(postCollection);
        // 有--->查看收藏记录状态
        if (collectionDetail != null) {
            Integer status = RedisConstants.getPostStatus(collectionDetail);
            // 状态为0，则改状态为1，收藏量+1
            if (PostStatus.UNCOLLECTION.getCode().equals(status)) {
                if (redisUtils.saveCollection(postCollection)) {
                    return Result.success();
                } else {
                    log.error("服务器出错，收藏修改失败");
                    return Result.error("修改失败");
                }
            }
            // 状态为1，则报错
            else return Result.error("您已经点赞过了");
        }
        // 没有--->在数据库中查询是否有该收藏记录
        PostCollection postCollection1 = this.lambdaQuery().eq(PostCollection::getPostId, postCollection.getPostId())
                .eq(PostCollection::getUid, postCollection.getUid()).one();
        // 有--->查看记录状态
        if (postCollection1 != null) {
            // 状态为0，则改状态为1，并保存在redis中，收藏量+1
            if (PostStatus.UNCOLLECTION.getCode().equals(postCollection1.getStatus())) {
                postCollection1.setStatus(postCollection.getStatus());
                if (redisUtils.saveCollection(postCollection)) {
                    return Result.success();
                } else {
                    log.error("服务器出错，收藏修改失败");
                    return Result.error("修改失败");
                }
            }
            // 状态为1，则报错
            else return Result.error("您已经收藏过了");
        }
        // 没有--->在redis中增加收藏记录，收藏+1
        redisUtils.saveCollection(postCollection);
        return Result.success();
    }

    public Result deleteCollection(PostCollection postCollection) {
        // 在redis中查询是否有该条收藏记录
        String collectionDetail = redisUtils.getCollectionDetail(postCollection);
        // 有--->查看收藏记录状态
        if (collectionDetail != null) {
            Integer status = RedisConstants.getPostStatus(collectionDetail);
            // 状态为1，则改状态为0，收藏量-1
            if (LIKE.getCode().equals(status)) {
                if (redisUtils.saveCollection(postCollection)) {
                    return Result.success();
                } else {
                    log.error("服务器出错，点赞修改失败");
                    return Result.error("修改失败");
                }
            }
            // 状态为0，则报错
            else return Result.error("您未收藏过");
        }
        // 没有--->在数据库中查询是否有该收藏记录
        PostCollection postCollection1 = this.lambdaQuery().eq(PostCollection::getPostId, postCollection.getPostId())
                .eq(PostCollection::getUid, postCollection.getUid()).one();

        // 有--->查看记录状态
        if (postCollection1 != null) {
            // 状态为1，则改状态为0，并保存在redis中，收藏量-1
            if (COLLECTION.getCode().equals(postCollection1.getStatus())) {
                postCollection1.setStatus(postCollection.getStatus());
                if (redisUtils.saveCollection(postCollection)) {
                    return Result.success();
                } else {
                    log.error("服务器出错，收藏修改失败");
                    return Result.error("修改失败");
                }
            }
            // 状态为0，则报错
            else return Result.error("您未收藏过");
        }
        // 没有--->从未收藏过，报错
        return Result.error("您未收藏过");
    }

    @Override
    @Transactional
    public void saveCollectionFromRedis() {
        // 将收藏记录包装成PostCollection对象集合
        ArrayList<PostCollection> collections = redisUtils.getPostCollections();
        if(collections.isEmpty()){
            return;
        }
        // 增强for一一处理
        for(PostCollection collection : collections){
            // 与mysql中的记录进行对比，去重
            PostCollection postCollection = lambdaQuery().eq(PostCollection::getPostId, collection.getPostId())
                    .eq(PostCollection::getUid, collection.getUid()).one();
            // 若没有记录则增加记录
            if(Objects.isNull(postCollection)){
                save(collection);
            }
            // 若有记录则更新记录
            else {
                postCollection.setStatus(collection.getStatus());
                updateById(postCollection);
            }
        }
    }

    @Override
    @Transactional
    public void saveCollectionNumFromRedis() {
        // 将redis中的收藏数记录封装成集合
        ArrayList<Post> postList = redisUtils.getPostCollectionList();
        if(postList.isEmpty()){
            return;
        }
        // 增强for一一处理
        for(Post postDTO : postList){
            Post post = postService.getOnePost(postDTO.getId());
            if(Objects.isNull(post)){
                return;
            }
            // 将mysql中的收藏数记录与redis中的进行相加
            Integer collectionNum = post.getCollections() + postDTO.getCollections();
            post.setCollections(collectionNum);
            // 更新mysql中的数据
            postService.updateById(post);
        }
    }

    @Override
    public Boolean isCollection(Integer postId, Integer uid) {
        Boolean collection = redisUtils.isCollection(postId, uid);
        if (Objects.isNull(collection)){
            PostCollection postCollection = lambdaQuery().eq(PostCollection::getPostId, postId)
                    .eq(PostCollection::getUid, uid).one();
            if(Objects.isNull(postCollection)){
                return false;
            }
            return LIKE.getCode().equals(postCollection.getStatus());
        }
        return collection;
    }

    @Override
    public List<PostCollection> getCollectedPostFromRedis(UserDTO user) {
        return redisUtils.getPostCollectionByUserId(user);
    }
}
