package com.zhengaicha.journey_of_poet.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.dto.UserDTO;
import com.zhengaicha.journey_of_poet.entity.PlotChapter;
import com.zhengaicha.journey_of_poet.entity.PlotEvent;
import com.zhengaicha.journey_of_poet.entity.Poem;
import com.zhengaicha.journey_of_poet.entity.UserInfo;
import com.zhengaicha.journey_of_poet.mapper.PoemMapper;
import com.zhengaicha.journey_of_poet.service.PlotChapterService;
import com.zhengaicha.journey_of_poet.service.PlotEventService;
import com.zhengaicha.journey_of_poet.service.PoemService;
import com.zhengaicha.journey_of_poet.service.UserInfoService;
import com.zhengaicha.journey_of_poet.utils.EsUtil;
import com.zhengaicha.journey_of_poet.utils.UserHolder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;


@Service
public class PoemServiceImpl extends ServiceImpl<PoemMapper, Poem> implements PoemService {

    @Resource
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private EsUtil esUtil;

    @Override
    public Result poem(Integer poemId) {
        Poem one = lambdaQuery().eq(Poem::getId, poemId).one();
        JSONObject entries = new JSONObject(one.getOther());
        String s = entries.toString();
        one.setJsonOther(entries);
        return Result.success(one);
    }

    @Override
    public Result search(String keywords, int currentPage) {
        if (currentPage < 1) {
            return Result.error("页码错误");
        }

        List<Poem> poems = esUtil.searchPoem(keywords, currentPage);
        return Result.success(poems);
    }

    @Override
    public Poem getPoemByPoetry(String poetry) {
        if (poetry.length() < 3) {
            return null;
        }
        SearchRequest request = new SearchRequest("poem");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.query(QueryBuilders.matchPhraseQuery("content", poetry));
        request.source(builder);

        SearchResponse search = null;
        try {
            search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SearchHits hits = search.getHits();
        if (Objects.isNull(hits)) {
            return null;
        }
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            Poem poem = BeanUtil.copyProperties(sourceAsMap, Poem.class);
            String s = poem.getContent().replaceAll("<.*?>", "")
                    .replaceAll("[\n\t\r\s]*", "").replaceAll("[，。！？,!?]", ",");
            String[] split = s.split(",");
            boolean contains = Arrays.asList(split).contains(poetry);
            if (contains) {
                return poem;
            }
        }
        return null;
    }

    @Override
    public Result getRecitation(String poemId) {
        // 获取诗词内容
        Poem one = lambdaQuery().eq(Poem::getId, Integer.parseInt(poemId)).one();
        String poemContent = one.getContent()
                .replaceAll("[\n\t\r\s]*", "")
                .replaceAll("<.*?>", "")
                .replaceAll("，", "，,")
                .replaceAll("。", "。,")
                .replaceAll("！", "！,")
                .replaceAll("？", "？,")
                .replaceAll("!", "！,");

        String[] poemContentArray = poemContent.split(",");

        // 生成准备挖空的随机数
        List<Integer> randomNums = new ArrayList<>();
        int range = (poemContentArray.length / 3) + 1;
        while (randomNums.size() <= range) {
            Integer num = (int) (Math.random() * (poemContentArray.length - 1));
            if (!randomNums.contains(num)) {
                randomNums.add(num);
            }
        }

        // 给随机数从小到大排序
        randomNums.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });

        List<String> positiveOrder = new ArrayList<>(); // 存贮正序挖空内容
        // 将诗词内容数组按正序挖空，生成答案
        for (Integer num : randomNums) {
            String s = poemContentArray[num];
            if (!(s.contains("、") || s.contains("：") || s.contains("（"))) {
                poemContentArray[num] = (s.length() - 1) + (s.charAt(s.length() - 1) + "");
                positiveOrder.add(s.replaceAll("[，。！？,!?]", ""));
            }
        }

        // 将挖空的数组进行更进一步细分
        List<String> positiveOrderList = new ArrayList<>();
        for (String s : positiveOrder) {
            int length = s.length();
            if (length < 4) {
                positiveOrderList.add(s);
            } else if (length % 2 == 0) {
                for (int i = 0; i < s.length() - 1; i = i + 2) {
                    positiveOrderList.add(s.substring(i, i + 2));
                }
            } else {
                int i = 0;
                for (; i < s.length() - 3; i = i + 2) {
                    positiveOrderList.add(s.substring(i, i + 2));
                }
                positiveOrderList.add(s.substring(i));
            }
        }
        int length = positiveOrderList.size();

        // 转成字符串数组
        String[] positiveOrder1 = new String[length];
        for (int i = 0; i < length; i++) {
            positiveOrder1[i] = positiveOrderList.get(i);
        }

        // 生成乱序随机数
        List<Integer> randomNum = new ArrayList<>();
        while (randomNum.size() < length / 2) {
            int random = (int) (Math.random() * (length - 1));
            if (!randomNum.contains(random)) {
                randomNum.add(random);
            }
        }

        String[] outOfOrder = new String[length];
        int i = 0;
        for (int random : randomNum) {
            outOfOrder[i] = positiveOrderList.get(random);
            positiveOrderList.set(random, null);
            i++;
        }
        i = length - 1;
        for (String s : positiveOrderList) {
            if (s != null) {
                outOfOrder[i] = s;
                i--;
            }
        }

        // 打包数据返回给前端
        HashMap<String, String[]> stringHashMap = new HashMap<>();
        stringHashMap.put("hollowedPoem", poemContentArray);
        stringHashMap.put("positiveOrder", positiveOrder1);
        stringHashMap.put("outOfOrder", outOfOrder);
        return Result.success(stringHashMap);
    }


}
