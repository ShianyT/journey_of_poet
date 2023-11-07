package com.zhengaicha.journey_of_poet.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.controller.BattleController;
import com.zhengaicha.journey_of_poet.dto.BattleUserDTO;
import com.zhengaicha.journey_of_poet.dto.PoetryDetailDTO;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.PoetryBattleDetail;
import com.zhengaicha.journey_of_poet.entity.PoetryBattleRecords;
import com.zhengaicha.journey_of_poet.entity.User;
import com.zhengaicha.journey_of_poet.mapper.PoetryBattleRecordsMapper;
import com.zhengaicha.journey_of_poet.service.PoetryBattleDetailService;
import com.zhengaicha.journey_of_poet.service.PoetryBattleRecordsService;
import com.zhengaicha.journey_of_poet.service.UserService;
import com.zhengaicha.journey_of_poet.utils.RedisUtils;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.websocket.Session;
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
public class PoetryBattleRecordsServiceImpl
        extends ServiceImpl<PoetryBattleRecordsMapper, PoetryBattleRecords>
        implements PoetryBattleRecordsService {

    @Autowired
    private PoetryBattleDetailService poetryBattleDetailService;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private UserService userService;
    private static boolean isStartMatch = false;
    private static final Queue<UserDTO> matchQueue = new LinkedList<>();
    /**
     * 正在对战中的用户队列
     */
    private final Queue<Integer> battleQueue = new LinkedList<>();

    private static final ArrayList<String> keywords = new ArrayList<>();


    static {
        keywords.add("本");
        keywords.add("致");
        keywords.add("其");
        keywords.add("蕴");
        keywords.add("水");
        keywords.add("间");
        keywords.add("日");
        keywords.add("观");
    }

    @Override
    public Result add() {
        if (!isStartMatch) {
            isStartMatch = true;
            match();
        }
        UserDTO user = UserHolder.getUser();
        synchronized (battleQueue) {
            if (battleQueue.contains(user.getUid())) {
                return Result.error("您正在游戏中");
            }
            battleQueue.notify();
        }
        synchronized (matchQueue) {
            if (matchQueue.contains(user)) {
                return Result.error("您已在匹配队列");
            }
            matchQueue.offer(user);
            matchQueue.notify();
        }
        log.info(user.getNickname() + "用户进入匹配阶段>>>");
        return Result.success("进入匹配阶段>>>");
    }

    @Override
    public void match() {
        new Thread(() -> {
            while (true) {
                handlerMatch();
            }
        }).start();
    }

    private void handlerMatch() {
        synchronized (matchQueue) {
            try {
                while (matchQueue.size() < 2) {
                    matchQueue.wait();
                }
                // 随机选取两个用户
                UserDTO user1 = matchQueue.poll();
                UserDTO user2 = matchQueue.poll();
                Integer uid1 = user1.getUid();
                Integer uid2 = user2.getUid();
                // 防止重复匹配到自己
                if (uid1.equals(uid2)) {
                    matchQueue.offer(user1);
                    return;
                }
                log.info("匹配出两个玩家: " + uid1 + "," + uid2);
                Session session1 = BattleController.getSession(uid1);
                Session session2 = BattleController.getSession(uid2);
                // 防止用户关闭通道
                if (!session1.isOpen()) {
                    matchQueue.offer(user2);
                    return;
                }
                if (!session2.isOpen()) {
                    matchQueue.offer(user1);
                    return;
                }
                synchronized (battleQueue) {
                    battleQueue.add(user1.getUid());
                    battleQueue.add(user2.getUid());
                    battleQueue.notify();
                }
                // 存储对战对象
                String keyword = keywords.get((int) (Math.random() * keywords.size()));
                savePoetryBattleRecords(keyword, user1, user2);

                log.info("匹配成功");
                ObjectMapper objectMapper = new ObjectMapper();
                String message = objectMapper.writeValueAsString(new BattleUserDTO(user2, true, keyword));
                // 发送匹配成功后的消息
                session1.getAsyncRemote().sendText(message);
                message = objectMapper.writeValueAsString(new BattleUserDTO(user1, false, keyword));
                session2.getAsyncRemote().sendText(message);

            } catch (InterruptedException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 在mysql与redis中分别存储对战记录
     */
    @Transactional
    public void savePoetryBattleRecords(String keyword, UserDTO user1, UserDTO user2) {
        // 创建对战记录对象
        PoetryBattleRecords poetryBattleRecords =
                new PoetryBattleRecords(user1.getUid(), user2.getUid(), keyword, new Timestamp(System.currentTimeMillis()));
        save(poetryBattleRecords);
        // 将完整的对战记录信息获取存入redis中，方便后续使用
        PoetryBattleRecords one = lambdaQuery().eq(PoetryBattleRecords::getBeforeUid, poetryBattleRecords.getBeforeUid())
                .orderByDesc(PoetryBattleRecords::getId).last("limit 1").one();
        one.setBeforeIcon(user1.getIcon());
        one.setBeforeNickname(user1.getNickname());
        one.setAfterIcon(user2.getIcon());
        one.setAfterNickname(user2.getNickname());
        redisUtils.savePoetryBattleRecords(one);
    }

    @Override
    public Result cancelMatch() {
        UserDTO user = UserHolder.getUser();
        synchronized (matchQueue) {
            matchQueue.remove(user);
            matchQueue.notify();
            return Result.success();
        }
    }

    @Override
    public Result history(int currentPage) {
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return Result.error("出错啦！请登录");
        }
        // 获取该用户所参与的对战记录列表
        Integer uid = user.getUid();
        List<PoetryBattleRecords> records = lambdaQuery()
                .eq(PoetryBattleRecords::getBeforeUid, uid)
                .or()
                .eq(PoetryBattleRecords::getAfterUid, uid)
                .page(new Page<>(currentPage, 10)).getRecords();
        if (records.isEmpty()) {
            return Result.error("已没有更多对战记录");
        }
        for (PoetryBattleRecords poetryBattleRecord : records) {
            User beforeUser = userService.getOne(poetryBattleRecord.getBeforeUid());
            poetryBattleRecord.setBeforeNickname(beforeUser.getNickname());
            poetryBattleRecord.setBeforeIcon(beforeUser.getIcon());
            User afterUser = userService.getOne(poetryBattleRecord.getAfterUid());
            poetryBattleRecord.setAfterNickname(afterUser.getNickname());
            poetryBattleRecord.setAfterIcon(afterUser.getIcon());
            List<PoetryBattleDetail> battleDetails = poetryBattleDetailService.getBattleDetail(poetryBattleRecord.getId());
            poetryBattleRecord.setPoetryBattleDetails(battleDetails);
        }
        return Result.success(records);
    }

    @Override
    public Result learn(String keyword) {
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return Result.error("出错啦！请登录");
        }
        Integer uid = user.getUid();
        List<PoetryBattleRecords> poetryBattleRecords = lambdaQuery()
                .eq(PoetryBattleRecords::getKeyword, keyword)
                .eq(PoetryBattleRecords::getBeforeUid, uid)
                .or()
                .eq(PoetryBattleRecords::getKeyword, keyword)
                .eq(PoetryBattleRecords::getAfterUid, uid)
                .list();
        List<PoetryDetailDTO> poetryDetailDTOS = new ArrayList<>();
        List<String> poem = new LinkedList<>();

        // 遍历用户所有的对战记录
        for (PoetryBattleRecords poetryBattleRecord : poetryBattleRecords) {
            List<PoetryBattleDetail> battleDetails = poetryBattleDetailService.getBattleDetail(poetryBattleRecord.getId());
            // 遍历对战详情
            for (PoetryBattleDetail poetryBattleDetail : battleDetails) {
                // 当该条对战详情有效
                if (!Objects.isNull(poetryBattleDetail.getPoemId())) {
                    PoetryDetailDTO poetryDetailDTO =
                            new PoetryDetailDTO(poetryBattleDetail.getPoem(), poetryBattleDetail.getPoemId(),1);
                    // 当该对战详情为当前用户所发送
                    if (poetryBattleDetail.getUid().equals(uid)) {
                        // 当在链表中不存在时
                        if (!poem.contains(poetryDetailDTO.getPoem())) {
                            poetryDetailDTOS.add(poetryDetailDTO);
                            poem.add(poetryDetailDTO.getPoem());
                        } else {
                            PoetryDetailDTO detailDTO = poetryDetailDTOS.get(poetryDetailDTOS.indexOf(poetryDetailDTO));
                            detailDTO.setCount(detailDTO.getCount() + 1);
                        }
                    } else {
                        if (!poem.contains(poetryDetailDTO.getPoem())) {
                            poetryDetailDTO.setCount(0);
                            poetryDetailDTOS.add(poetryDetailDTO);
                            poem.add(poetryDetailDTO.getPoem());
                        }
                    }
                }
            }
        }
        return Result.success(poetryDetailDTOS);
    }

    @Override
    @Transactional
    public Result outcome(PoetryBattleRecords poetryBattleRecords) {
        if (Objects.isNull(poetryBattleRecords.getBeforeUid())
                || Objects.isNull(poetryBattleRecords.getAfterUid())
                || Objects.isNull(poetryBattleRecords.getOutcome())) {
            return Result.error("传入参数不足");
        }
        synchronized (battleQueue) {
            battleQueue.remove(poetryBattleRecords.getBeforeUid());
            battleQueue.remove(poetryBattleRecords.getAfterUid());
            battleQueue.notify();
        }
        // 从redis中取出对战记录
        PoetryBattleRecords poetryBattleRecord =
                redisUtils.getPoetryBattleRecords(poetryBattleRecords.getBeforeUid(), poetryBattleRecords.getAfterUid());
        if (Objects.isNull(poetryBattleRecord)) {
            return Result.error("没有对战记录");
        }

        // 将对战结果存储在mysql
        lambdaUpdate().eq(PoetryBattleRecords::getId, poetryBattleRecord.getId())
                .set(PoetryBattleRecords::getOutcome, poetryBattleRecords.getOutcome()).update();

        // 设置对战结果并将对战详情保存
        poetryBattleRecord.setOutcome(poetryBattleRecords.getOutcome());
        poetryBattleDetailService.saveBattleDetail(poetryBattleRecord);

        // 获取用户对战详情进行结果返回
        List<PoetryBattleDetail> poetryBattleDetails = poetryBattleDetailService.getBattleDetail(poetryBattleRecord.getId());
        return Result.success(poetryBattleDetails);
    }
}
