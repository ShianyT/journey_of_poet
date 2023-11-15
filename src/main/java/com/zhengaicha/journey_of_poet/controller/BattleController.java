package com.zhengaicha.journey_of_poet.controller;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.PoetryBattleDetail;
import com.zhengaicha.journey_of_poet.service.PoetryBattleDetailService;
import com.zhengaicha.journey_of_poet.utils.RedisUtils;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint(value = "/battles/send")
@Slf4j
public class BattleController {

    private static PoetryBattleDetailService poetryBattleDetailService;


    private static RedisUtils redisUtils;

    @Autowired
    public void setRedisUtils(RedisUtils redisUtils) {
        BattleController.redisUtils = redisUtils;
    }



    @Autowired
    public void setPoetryBattleDetailService(PoetryBattleDetailService poetryBattleDetailService){
        BattleController.poetryBattleDetailService = poetryBattleDetailService;
    }

    private Integer userUid;
    private Session session;

    private static CopyOnWriteArraySet<BattleController> webSockets = new CopyOnWriteArraySet<>();
    private static ConcurrentHashMap<Integer, Session> sessionPool = new ConcurrentHashMap<>();

    /**
     * 链接成功时进行的操作
     */
    @OnOpen
    public void onOpen(Session session) {
        UserDTO user = UserHolder.getUser();
        if(Objects.isNull(user)){
            JSONObject accumulate = new JSONObject().accumulate("error", "出错啦！请登录");
            sendToSelf(accumulate.toString());
            return;
        }
        try {
            this.session = session;
            this.userUid = user.getUid();
            webSockets.add(this);
            sessionPool.put(userUid, session);
            log.info("有新的连接，总数为：" + webSockets.size());
        } catch (Exception ignored) {
        }
    }

    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            webSockets.remove(this);
            sessionPool.remove(this.userUid);
            log.info("连接断开，总数为：" + webSockets.size());
        } catch (Exception e) {
        }
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("收到报文：" + message);

        if(!redisUtils.isBattling(userUid)){
            JSONObject accumulate = new JSONObject().accumulate("error", "您未处于游戏状态");
            sendToSelf(accumulate.toString());
            return;
        }

        JSONObject json = new JSONObject(message);
        String poem = (String) json.get("message");
        if(poem.isEmpty() || json.get("uid") == null){
            JSONObject accumulate = new JSONObject().accumulate("error", "输入错误");
            sendToSelf(accumulate.toString());
            return;
        }
        Integer uid = null;
        try {
            uid = (Integer) json.get("uid");
        } catch (NumberFormatException e) {
            JSONObject accumulate = new JSONObject().accumulate("error", "对方uid传入错误");
            sendToSelf(accumulate.toString());
        }
        PoetryBattleDetail poetryBattleDetail = poetryBattleDetailService.matchPoem(userUid,poem);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String message1 = objectMapper.writeValueAsString(poetryBattleDetail);
            sendToSelf(message1);
            sendToMassage(uid,message1);
        } catch (NumberFormatException e) {
            JSONObject accumulate = new JSONObject().accumulate("error", "对方uid传入错误");
            sendToSelf(accumulate.toString());
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 发送错误时的处理
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误，原因：\n" + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 广播消息
     */
    public void sendAllMessage(String message) {
        log.info("广播消息：" + message);
        for (BattleController webSocket : webSockets) {
            try {
                if (webSocket.session.isOpen()) {
                    webSocket.session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送消息给对方用户
     */
    public void sendToMassage(Integer uid,String message) {
        Session session1 = sessionPool.get(uid);
        if (session1 != null && session1.isOpen()) {
            try {
                session1.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送信息给自己
     */
    public void sendToSelf(String message) {
        if (session != null && session.isOpen()) {
            try {
                if(Objects.isNull(message)){
                    JSONObject accumulate = new JSONObject().accumulate("error", "出错啦！请登录");
                    session.getAsyncRemote().sendText(accumulate.toString());
                }
                session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 单点消息（多人）
     */
    public void sendMoreMessage(Integer[] uids, String message) {
        for (Integer uid : uids) {
            Session session = sessionPool.get(uid);
            if (session != null && session.isOpen()) {
                try {
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Session getSession(Integer uid) {
        return sessionPool.get(uid);
    }
}


