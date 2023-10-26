package com.zhengaicha.journey_of_poet.utils;

import cn.hutool.core.util.StrUtil;

import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.Post;
import com.zhengaicha.journey_of_poet.entity.PostCollection;
import com.zhengaicha.journey_of_poet.entity.PostLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.sql.Timestamp;
import java.util.*;

import static com.zhengaicha.journey_of_poet.utils.PostStatus.*;
import static com.zhengaicha.journey_of_poet.utils.RedisConstants.*;
import static com.zhengaicha.journey_of_poet.utils.SystemConstants.USER_AUTHORIZATION;

@Service("redisUtils")
public class RedisUtils {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private HashOperations<String, String, Object> HashOperations;

    /**
     * 存入单个hash类型缓存
     */
    public void hPut(String key, String filed, String value) {
        stringRedisTemplate.opsForHash().put(key, filed, value);
    }

    /**
     * 获取完整tokenKey
     */
    public String getTokenKey(String token) {
        if (StringUtils.hasText(token))
            return LOGIN_TOKEN_KEY + token;
        return null;
    }

    /**
     * 根据请求获取token
     */
    private String getTokenKey(HttpServletRequest request) {
        String token = request.getHeader(USER_AUTHORIZATION);
        if (StrUtil.isBlank(token)) {
            return null;
        }
        return LOGIN_TOKEN_KEY + token;
    }

    /**
     * 删除token
     */
    public boolean deleteToken(HttpServletRequest request) {
        String tokenKey = getTokenKey(request);
        if (tokenKey != null) {
            stringRedisTemplate.delete(tokenKey);
            return true;
        }
        return false;
    }

    /**
     * 刷新redis中，token对应的用户信息
     */
    public boolean flushTokenData(String field, String newContent, HttpServletRequest request) {
        String tokenKey = getTokenKey(request);
        if (tokenKey != null) {
            stringRedisTemplate.opsForHash().put(tokenKey, field, newContent);
            return true;
        }
        return false;
    }

    /**
     * 删除一整个key
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    /**
     * 获取likeDetailValue
     */
    public String getLikeDetail(PostLike postLike) {
        return (String) stringRedisTemplate.opsForHash().get(POST_LIKE_DETAIL_KEY,
                RedisConstants.getPostKey(postLike.getPostId(), postLike.getUid()));
    }

    /**
     * 获取CollectionDetailValue
     */
    public String getCollectionDetail(PostCollection postCollection) {
        return (String) stringRedisTemplate.opsForHash().get(POST_COLLECTION_DETAIL_KEY,
                RedisConstants.getPostKey(postCollection.getPostId(), postCollection.getUid()));
    }

    /**
     * 在redis中保存点赞记录
     */
    public boolean saveLike(PostLike postLike) {
        try {
            String likeKey = getPostKey(postLike.getPostId(), postLike.getUid());
            String likeNumKey = getPostNumKey(postLike.getPostId());
            // 增加记录
            stringRedisTemplate.opsForHash().put(POST_LIKE_DETAIL_KEY, likeKey,
                    RedisConstants.getPostDetailValue(postLike.getStatus(), new Timestamp(System.currentTimeMillis())));
            Object likeNumObject = HashOperations.get(POST_LIKE_NUM_KEY, likeNumKey);
            int likeNum = 0;
            if (Objects.isNull(likeNumObject)) {
                HashOperations.put(POST_LIKE_NUM_KEY, likeNumKey, likeNum);
                // stringRedisTemplate.opsForHash().put(POST_LIKE_NUM_KEY, likeNumKey, likeNum);
            } else {
                likeNum = (int) likeNumObject;
            }
            // 点赞数量增加
            if (LIKE.getCode().equals(postLike.getStatus())) {
                HashOperations.put(POST_LIKE_NUM_KEY, likeNumKey, likeNum + 1);
            }
            // 点赞数量减少
            if (UNLIKE.getCode().equals(postLike.getStatus())) {
                HashOperations.put(POST_LIKE_NUM_KEY, likeNumKey, likeNum - 1);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 在redis中保存收藏记录
     */
    public boolean saveCollection(PostCollection postCollection) {
        try {
            String collectionKey = getPostKey(postCollection.getPostId(), postCollection.getUid());
            String collectionNumKey = getPostNumKey(postCollection.getPostId());
            // 增加记录
            stringRedisTemplate.opsForHash().put(POST_COLLECTION_DETAIL_KEY, collectionKey,
                    RedisConstants.getPostDetailValue(postCollection.getStatus(), new Timestamp(System.currentTimeMillis())));
            Object collectionNumObject = HashOperations.get(POST_COLLECTION_NUM_KEY, collectionNumKey);
            int collectionNum = 0;
            if (Objects.isNull(collectionNumObject)) {
                HashOperations.put(POST_COLLECTION_NUM_KEY, collectionNumKey, collectionNum);
                // stringRedisTemplate.opsForHash().put(POST_LIKE_NUM_KEY, collectionNumKey, collectionNum);
            } else {
                collectionNum = (int) collectionNumObject;
            }
            // 点赞数量增加
            if (COLLECTION.getCode().equals(postCollection.getStatus())) {
                HashOperations.put(POST_COLLECTION_NUM_KEY, collectionNumKey, collectionNum + 1);
            }
            // 点赞数量减少
            if (UNCOLLECTION.getCode().equals(postCollection.getStatus())) {
                HashOperations.put(POST_COLLECTION_NUM_KEY, collectionNumKey, collectionNum - 1);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取PostLike集合并清除like缓存
     */
    public ArrayList<PostLike> getPostLikes() {
        // 获取游标
        Cursor<Map.Entry<Object, Object>> LikesCursor = stringRedisTemplate.opsForHash()
                .scan(POST_LIKE_DETAIL_KEY, ScanOptions.NONE);
        ArrayList<PostLike> postLikes = new ArrayList<>();
        // 遍历游标，获取包装PostLike对象
        while (LikesCursor.hasNext()) {
            PostLike postLike = new PostLike();
            Map.Entry<Object, Object> LikeEntry = LikesCursor.next();

            String likeKey = (String) LikeEntry.getKey();
            String[] split = likeKey.split("::");
            // postId
            postLike.setPostId(Integer.valueOf(split[0]));
            // uid
            postLike.setUid(Integer.valueOf(split[1]));

            String likeValue = (String) LikeEntry.getValue();
            String[] split1 = likeValue.split("::");
            // status
            postLike.setStatus(Integer.valueOf(split1[0]));
            // createTime
            postLike.setCreateTime(Timestamp.valueOf(split1[1]));
            postLikes.add(postLike);
        }
        stringRedisTemplate.delete(POST_LIKE_DETAIL_KEY);
        return postLikes;
    }

    /**
     * 获取PostCollection集合并清除collection缓存
     */
    public ArrayList<PostCollection> getPostCollections() {
        // 获取游标
        Cursor<Map.Entry<Object, Object>> CollectionsCursor = stringRedisTemplate.opsForHash()
                .scan(POST_COLLECTION_DETAIL_KEY, ScanOptions.NONE);
        ArrayList<PostCollection> postCollections = new ArrayList<>();
        // 遍历游标，获取包装PostLike对象
        while (CollectionsCursor.hasNext()) {
            PostCollection postCollection = new PostCollection();
            Map.Entry<Object, Object> collectionEntry = CollectionsCursor.next();

            String collectionKey = (String) collectionEntry.getKey();
            String[] split = collectionKey.split("::");
            // postId
            postCollection.setPostId(Integer.valueOf(split[0]));
            // uid
            postCollection.setUid(Integer.valueOf(split[1]));

            String collectionValue = (String) collectionEntry.getValue();
            String[] split1 = collectionValue.split("::");
            // status
            postCollection.setStatus(Integer.valueOf(split1[0]));
            // createTime
            postCollection.setCreateTime(Timestamp.valueOf(split1[1]));
            postCollections.add(postCollection);
        }
        stringRedisTemplate.delete(POST_COLLECTION_DETAIL_KEY);
        return postCollections;
    }

    /**
     * 获取redis中的点赞列表
     */
    public ArrayList<Post> getPostLikeList() {
        // 获取游标
        Cursor<Map.Entry<String, Object>> LikesCursor = HashOperations.scan(POST_LIKE_NUM_KEY, ScanOptions.NONE);
        ArrayList<Post> posts = new ArrayList<>();
        // 遍历游标，获取包装PostLike对象
        while (LikesCursor.hasNext()) {
            Post post = new Post();
            Map.Entry<String, Object> LikeEntry = LikesCursor.next();

            // postId
            String likeEntryKey = LikeEntry.getKey();
            post.setId(Integer.valueOf(likeEntryKey));

            Integer likeNum = (Integer) LikeEntry.getValue();
            post.setLikes(likeNum);
            posts.add(post);
        }
        stringRedisTemplate.delete(POST_LIKE_NUM_KEY);
        return posts;
    }

    /**
     * 查询用户是否点赞
     */
    public Boolean isLike(Integer postId, Integer uid) {
        String likeKey = getPostKey(postId, uid);
        String likeValue = (String) stringRedisTemplate.opsForHash().get(POST_LIKE_DETAIL_KEY, likeKey);
        if (Objects.isNull(likeValue)) {
            return null;
        }
        Integer status = getPostStatus(likeValue);
        return LIKE.getCode().equals(status);
    }

    /**
     * 获取点赞数
     */
    public Integer getLikeNum(Post post) {
        Integer likeNum = (Integer) HashOperations.get(POST_LIKE_NUM_KEY, RedisConstants.getPostNumKey(post.getId()));
        if (Objects.isNull(likeNum)) {
            return 0;
        }
        return likeNum;
    }

    /**
     * 获取redis中的收藏数列表
     */
    public ArrayList<Post> getPostCollectionList() {
        // 获取游标
        Cursor<Map.Entry<String, Object>> collectionsCursor = HashOperations.scan(POST_COLLECTION_NUM_KEY, ScanOptions.NONE);
        ArrayList<Post> posts = new ArrayList<>();
        // 遍历游标，获取包装Post对象
        while (collectionsCursor.hasNext()) {
            Post post = new Post();
            Map.Entry<String, Object> collectionEntry = collectionsCursor.next();

            // postId
            String collectionEntryKey = collectionEntry.getKey();
            post.setId(Integer.valueOf(collectionEntryKey));

            Integer collectionNum = (Integer) collectionEntry.getValue();
            post.setCollections(collectionNum);
            posts.add(post);
        }
        stringRedisTemplate.delete(POST_COLLECTION_NUM_KEY);
        return posts;
    }

    /**
     * 查询用户是否收藏
     */
    public Boolean isCollection(Integer postId, Integer uid) {
        String collectionKey = getPostKey(postId, uid);
        String collectionValue = (String) stringRedisTemplate.opsForHash().get(POST_COLLECTION_DETAIL_KEY, collectionKey);
        if (Objects.isNull(collectionValue)) {
            return null;
        }
        Integer status = getPostStatus(collectionValue);
        return COLLECTION.getCode().equals(status);
    }

    /**
     * 获取收藏数
     */
    public Integer getCollectionNum(Post post) {
        Integer collectionNum = (Integer) HashOperations.get(POST_COLLECTION_NUM_KEY, RedisConstants.getPostNumKey(post.getId()));
        if (Objects.isNull(collectionNum)) {
            return 0;
        }
        return collectionNum;
    }

    public List<PostCollection> getPostCollectionByUserId(UserDTO user) {
        List<PostCollection> postCollections = new ArrayList<>();
        Set<String> keys = HashOperations.keys(POST_COLLECTION_DETAIL_KEY);
        for(String key : keys){
            String uidStr = key.split("::")[1];
            int uid = Integer.parseInt(uidStr);
            if(user.getUid().equals(uid)){
                PostCollection postCollection = new PostCollection();
                String postIdStr = key.split("::")[0];
                postCollection.setPostId(Integer.parseInt(postIdStr));
                postCollections.add(postCollection);
            }
        }
        return postCollections;
    }
}
