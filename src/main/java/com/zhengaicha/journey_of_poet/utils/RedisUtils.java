package com.zhengaicha.journey_of_poet.utils;

import cn.hutool.core.util.StrUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.*;
import com.zhengaicha.journey_of_poet.constants.BattleStatus;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.sql.Timestamp;
import java.util.*;

import static com.zhengaicha.journey_of_poet.constants.PostStatus.*;
import static com.zhengaicha.journey_of_poet.utils.RedisConstants.*;
import static com.zhengaicha.journey_of_poet.utils.SystemConstants.USER_AUTHORIZATION;

@Service
public class RedisUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private HashOperations<String, String, Object> HashOperations;

    @Autowired
    private UserInfoService userInfoService;

    private final ObjectMapper objectMapper = new ObjectMapper();


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
            return LOGIN_TOKEN_KEY_PREFIX + token;
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
        return LOGIN_TOKEN_KEY_PREFIX + token;
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
        try {
            String tokenKey = getTokenKey(request);
            if (tokenKey != null) {
                UserDTO user = UserHolder.getUser();
                if (field.equals("nickname")) {
                    user.setNickname(newContent);
                }
                if (field.equals("icon")) {
                    user.setIcon(newContent);
                }
                String value = objectMapper.writeValueAsString(user);
                stringRedisTemplate.opsForValue().set(tokenKey, value);
                return true;
            }
            return false;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
        return (String) stringRedisTemplate.opsForHash().get(POST_LIKE_DETAIL_KEY_PREFIX,
                RedisConstants.getPostKey(postLike.getPostId(), postLike.getUid()));
    }

    /**
     * 获取CollectionDetailValue
     */
    public String getCollectionDetail(PostCollection postCollection) {
        return (String) stringRedisTemplate.opsForHash().get(POST_COLLECTION_DETAIL_KEY_PREFIX,
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
            stringRedisTemplate.opsForHash().put(POST_LIKE_DETAIL_KEY_PREFIX, likeKey,
                    RedisConstants.getPostDetailValue(postLike.getStatus(), new Timestamp(System.currentTimeMillis())));
            Object likeNumObject = HashOperations.get(POST_LIKE_NUM_KEY_PREFIX, likeNumKey);
            int likeNum = 0;
            if (Objects.isNull(likeNumObject)) {
                HashOperations.put(POST_LIKE_NUM_KEY_PREFIX, likeNumKey, likeNum);
                // stringRedisTemplate.opsForHash().put(POST_LIKE_NUM_KEY, likeNumKey, likeNum);
            } else {
                likeNum = (int) likeNumObject;
            }
            // 点赞数量增加
            if (LIKE.getCode().equals(postLike.getStatus())) {
                HashOperations.put(POST_LIKE_NUM_KEY_PREFIX, likeNumKey, likeNum + 1);
            }
            // 点赞数量减少
            if (UNLIKE.getCode().equals(postLike.getStatus())) {
                HashOperations.put(POST_LIKE_NUM_KEY_PREFIX, likeNumKey, likeNum - 1);
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
            stringRedisTemplate.opsForHash().put(POST_COLLECTION_DETAIL_KEY_PREFIX, collectionKey,
                    RedisConstants.getPostDetailValue(postCollection.getStatus(), new Timestamp(System.currentTimeMillis())));
            Object collectionNumObject = HashOperations.get(POST_COLLECTION_NUM_KEY_PREFIX, collectionNumKey);
            int collectionNum = 0;
            if (Objects.isNull(collectionNumObject)) {
                HashOperations.put(POST_COLLECTION_NUM_KEY_PREFIX, collectionNumKey, collectionNum);
                // stringRedisTemplate.opsForHash().put(POST_LIKE_NUM_KEY, collectionNumKey, collectionNum);
            } else {
                collectionNum = (int) collectionNumObject;
            }
            // 点赞数量增加
            if (COLLECTION.getCode().equals(postCollection.getStatus())) {
                HashOperations.put(POST_COLLECTION_NUM_KEY_PREFIX, collectionNumKey, collectionNum + 1);
            }
            // 点赞数量减少
            if (UNCOLLECTION.getCode().equals(postCollection.getStatus())) {
                HashOperations.put(POST_COLLECTION_NUM_KEY_PREFIX, collectionNumKey, collectionNum - 1);
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
                .scan(POST_LIKE_DETAIL_KEY_PREFIX, ScanOptions.NONE);
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
        stringRedisTemplate.delete(POST_LIKE_DETAIL_KEY_PREFIX);
        return postLikes;
    }

    /**
     * 获取PostCollection集合并清除collection缓存
     */
    public ArrayList<PostCollection> getPostCollections() {
        // 获取游标
        Cursor<Map.Entry<Object, Object>> CollectionsCursor = stringRedisTemplate.opsForHash()
                .scan(POST_COLLECTION_DETAIL_KEY_PREFIX, ScanOptions.NONE);
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
        stringRedisTemplate.delete(POST_COLLECTION_DETAIL_KEY_PREFIX);
        return postCollections;
    }

    /**
     * 获取redis中的点赞列表
     */
    public ArrayList<Post> getPostLikeList() {
        // 获取游标
        Cursor<Map.Entry<String, Object>> LikesCursor = HashOperations.scan(POST_LIKE_NUM_KEY_PREFIX, ScanOptions.NONE);
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
        stringRedisTemplate.delete(POST_LIKE_NUM_KEY_PREFIX);
        return posts;
    }

    /**
     * 查询用户是否点赞
     */
    public Boolean isLike(Integer postId, Integer uid) {
        String likeKey = getPostKey(postId, uid);
        String likeValue = (String) stringRedisTemplate.opsForHash().get(POST_LIKE_DETAIL_KEY_PREFIX, likeKey);
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
        Integer likeNum = (Integer) HashOperations.get(POST_LIKE_NUM_KEY_PREFIX, RedisConstants.getPostNumKey(post.getId()));
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
        Cursor<Map.Entry<String, Object>> collectionsCursor = HashOperations.scan(POST_COLLECTION_NUM_KEY_PREFIX, ScanOptions.NONE);
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
        stringRedisTemplate.delete(POST_COLLECTION_NUM_KEY_PREFIX);
        return posts;
    }

    /**
     * 查询用户是否收藏
     */
    public Boolean isCollection(Integer postId, Integer uid) {
        String collectionKey = getPostKey(postId, uid);
        String collectionValue = (String) stringRedisTemplate.opsForHash().get(POST_COLLECTION_DETAIL_KEY_PREFIX, collectionKey);
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
        Integer collectionNum = (Integer) HashOperations.get(POST_COLLECTION_NUM_KEY_PREFIX, RedisConstants.getPostNumKey(post.getId()));
        if (Objects.isNull(collectionNum)) {
            return 0;
        }
        return collectionNum;
    }

    public List<PostCollection> getPostCollectionByUserId(UserDTO user) {
        List<PostCollection> postCollections = new ArrayList<>();
        Set<String> keys = HashOperations.keys(POST_COLLECTION_DETAIL_KEY_PREFIX);
        for (String key : keys) {
            String uidStr = key.split("::")[1];
            int uid = Integer.parseInt(uidStr);
            if (user.getUid().equals(uid)) {
                PostCollection postCollection = new PostCollection();
                String postIdStr = key.split("::")[0];
                postCollection.setPostId(Integer.parseInt(postIdStr));
                postCollections.add(postCollection);
            }
        }
        return postCollections;
    }


    /**
     * 存储对战记录对象
     */
    public void savePoetryBattleRecords(PoetryBattleRecords poetryBattleRecords) {
        try {
            String json = objectMapper.writeValueAsString(poetryBattleRecords);
            String battleRecordKey = getBattleRecordKey(poetryBattleRecords.getBeforeUid(), poetryBattleRecords.getAfterUid());
            // 存储对象在redis中
            stringRedisTemplate.opsForValue().set(battleRecordKey, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 获取对战记录
     */
    @Transactional
    public synchronized PoetryBattleRecords getPoetryBattleRecords(Integer beforeUid, Integer afterUid) {
        try {
            String battleRecordKey = getBattleRecordKey(beforeUid, afterUid);
            String json = stringRedisTemplate.opsForValue().get(battleRecordKey);
            if (Objects.isNull(json))
                return null;
            PoetryBattleRecords poetryBattleRecords = objectMapper.readValue(json, PoetryBattleRecords.class);
            // 判断用户是否已经使用过一次该记录
            if (poetryBattleRecords.isUse()) {
                stringRedisTemplate.delete(battleRecordKey);
            }
            // 若未使用过则设置为使用过，但暂不删除其在redis中的记录
            else {
                poetryBattleRecords.setUse(true);
                String value = objectMapper.writeValueAsString(poetryBattleRecords);
                stringRedisTemplate.opsForValue().set(battleRecordKey, value);
                poetryBattleRecords.setUse(false);
            }
            return poetryBattleRecords;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 检查用户是否重复发送相同诗句，之后存储该对战详情对象
     */
    public boolean savePoetryBattleDetail(Integer poetryBattleRecordId, PoetryBattleDetail poetryBattleDetail) {
        try {
            // 获取kye
            String battleDetailKey = getBattleDetailKey(poetryBattleRecordId);
            // 获取value
            String json = objectMapper.writeValueAsString(poetryBattleDetail);
            // 判断是否重复
            if (Objects.isNull(stringRedisTemplate.opsForHash().get(battleDetailKey, poetryBattleDetail.getPoem()))) {
                stringRedisTemplate.opsForHash().put(battleDetailKey, poetryBattleDetail.getPoem(), json);
            } else return false;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 获取用户的对战详情并清除缓存
     */
    @Transactional
    public List<PoetryBattleDetail> getPoetryBattleDetail(Integer poetryBattleRecordId) {
        try {
            String battleDetailKey = getBattleDetailKey(poetryBattleRecordId);
            long size = stringRedisTemplate.opsForHash().size(battleDetailKey);
            // 创建对战记录链表
            List<PoetryBattleDetail> poetryBattleDetails = new ArrayList<>();
            // 判断对战详情是否为空
            if (size < 1) {
                return null;
            }
            // 取出对战详情
            List<Object> values = stringRedisTemplate.opsForHash().values(battleDetailKey);
            stringRedisTemplate.delete(battleDetailKey);
            if (values.isEmpty()) {
                return null;
            }
            // 将value转换成PoetryBattleDetail对象并存入链表
            for (Object json : values) {
                PoetryBattleDetail poetryBattleDetail = objectMapper.readValue((String) json, PoetryBattleDetail.class);
                poetryBattleDetails.add(poetryBattleDetail);
            }
            return poetryBattleDetails;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断用户是否在对战列表中
     */
    public boolean isBattling(Integer uid) {
        String battleUserKey = getBattleUserKey(uid);
        String json = stringRedisTemplate.opsForValue().get(battleUserKey);
        if (json == null) {
            savePoetryBattleUser(uid);
            return false;
        }
        UserInfo userInfo = null;
        try {
            userInfo = objectMapper.readValue(json, UserInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Objects.equals(userInfo.getBattleStatue(), BattleStatus.IN_BATTLE);
    }

    /**
     * 往redis中保存对战用户
     */
    private void savePoetryBattleUser(Integer uid) {
        try {
            UserInfo one = userInfoService.lambdaQuery().eq(UserInfo::getUid, uid).one();
            String json = objectMapper.writeValueAsString(one);
            String battleUserKey = getBattleUserKey(uid);
            stringRedisTemplate.opsForValue().set(battleUserKey, json);
            // TODO 长周期清理用户记录
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void changeBattleUserStatue(Integer uid) {
        try {
            // 取出userInfo对象
            String battleUserKey = getBattleUserKey(uid);
            String json = stringRedisTemplate.opsForValue().get(battleUserKey);
             UserInfo userInfo = objectMapper.readValue(json, UserInfo.class);
            // 将其对战状态转换
            if (Objects.equals(userInfo.getBattleStatue(), BattleStatus.IN_BATTLE)) {
                userInfo.setBattleStatue(BattleStatus.NOT_IN_BATTLE);
            } else userInfo.setBattleStatue(BattleStatus.IN_BATTLE);
            // 重新存入redis
            json = objectMapper.writeValueAsString(userInfo);
            stringRedisTemplate.opsForValue().set(battleUserKey, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
