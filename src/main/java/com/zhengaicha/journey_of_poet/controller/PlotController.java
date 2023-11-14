package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.service.PlotService;
import com.zhengaicha.journey_of_poet.service.PoemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plots")
@Api(tags = "剧情相关接口")
public class PlotController {
    @Autowired
    private PlotService plotService;

    @Autowired
    private PoemService poemService;

    @GetMapping("/content/{eventId}/{nextPlotId}")
    @ApiOperation(value = "获得剧情文本", notes = "获取一条游戏文本，当遇到背诵时再另外发出请求；在该事件的第一次请求时，contentId给0")
    public Result getPlotContent(@ApiParam("当前事件节点id") @PathVariable Integer eventId
            , @ApiParam("当前所处于的剧情文本中的nextPlotId") @PathVariable String nextPlotId) {
        return plotService.getPlotContent(eventId, nextPlotId);
    }


    @GetMapping("/recite/{poemId}")
    @ApiOperation(value = "获取背诵的诗词文本", notes = "")
    public Result getRecitation(@ApiParam("传入\"背诵:\"后面的数字") @PathVariable String poemId) {
        return poemService.getRecitation(poemId);
    }

    @GetMapping("/progress")
    @ApiOperation(value = "获取当前用户的剧情进度")
    public Result getPlotProgress() {
        return plotService.getPlotProgress();
    }

    @PostMapping("/save")
    @ApiOperation(value = "保存当前用户的剧情进度", notes = "可以在每次章节结束之后放一次，" +
            "这边已经做过判断，即使返回之前的章节也不会重新存档")
    public Result savePlotProgress(@ApiParam("当前事件id") @RequestParam Integer eventId) {
        return plotService.savePlotProgress(eventId);
    }

    @GetMapping ("/unlock/{chapterId}")
    @ApiOperation(value = "解锁剧情章节", notes = "传入章节id")
    public Result unlockChapter(@ApiParam("解锁的章节id") @PathVariable Integer chapterId){
        return plotService.unlockChapter(chapterId);
    }
}
