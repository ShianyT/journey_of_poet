package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.PoetryBattleRecords;
import com.zhengaicha.journey_of_poet.service.PoemService;
import com.zhengaicha.journey_of_poet.service.PoetryBattleRecordsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/battles")
@Api(tags = "飞花令相关接口")
public class PoetryBattleController {

    @Autowired
    private PoetryBattleRecordsService poetryBattleRecordsService;

    @Autowired
    private PoemService poemService;

    /**
     * 匹配用户
     */
    @ApiOperation(value = "匹配用户", notes = "匹配对战用户，若成功则返回匹配用户，超时则失败")
    @GetMapping("/match")
    public Result add() {
        return poetryBattleRecordsService.add();
    }

    /**
     * 取消匹配
     */
    @ApiOperation(value = "用户取消匹配")
    @DeleteMapping
    public Result cancelMatch() {
        return poetryBattleRecordsService.cancelMatch();
    }

    /**
     * 对战结束
     */
    @ApiOperation(value = "对战结束", notes = "对战结束后，获得对战结果并生成记录")
    @PostMapping("/outcome")
    public Result outcome(@ApiParam(value = "传入beforeUid,afterUid,outcome") @RequestBody PoetryBattleRecords poetryBattleRecords) {
        return poetryBattleRecordsService.outcome(poetryBattleRecords);
    }

    /**
     * 获取历史对战记录
     */
    @ApiOperation(value = "获取用户个人历史对战记录",notes = "传入页数，一次十条")
    @GetMapping("/history/{currentPage}")
    public Result history(@PathVariable int currentPage) {
        return poetryBattleRecordsService.history(currentPage);
    }

    /**
     * 获取用户学习记录
     */
    @ApiOperation(value = "获取用户学习记录",notes = "传入关键字")
    @GetMapping("/learn/{keyword}")
    public Result learn(@PathVariable String keyword) {
        return poetryBattleRecordsService.learn(keyword);
    }


    /**
     * 获取诗歌详情
     */
    @ApiOperation(value = "获取诗歌详情")
    @GetMapping("/poem/{poemId}")
    public Result poem(@PathVariable Integer poemId) {
        return poemService.poem(poemId);
    }

    /**
     * 通过关键字搜索诗词
     */
    @ApiOperation(value = "通过关键字搜索诗词",notes = "直接传数据，不用转成json")
    @PostMapping("/search")
    public Result search(@ApiParam(value = "关键字") @RequestParam String keywords
            ,@ApiParam(value = "当前页数，一次15条") @RequestParam int currentPage) {
        return poemService.search(keywords,currentPage);
    }

    @GetMapping("/keywords")
    public Result getKeywords(){
        return poetryBattleRecordsService.getKeywords();
    }


}
