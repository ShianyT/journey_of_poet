package com.zhengaicha.journey_of_poet.controller;

import com.zhengaicha.journey_of_poet.dto.Result;
import com.zhengaicha.journey_of_poet.entity.Post;
import com.zhengaicha.journey_of_poet.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/posts")
@Api(tags = "对于帖子的增删改查接口")
public class PostController {
    @Autowired
    private PostService postService;

    @ApiOperation(value = "用户删除帖子", notes = "路径传参，第一个参数为第几页，第二个参数为该页的第几个，从0开始")
    @DeleteMapping("/{currentPage}/{index}")
    public Result deletePost(@PathVariable Integer currentPage, @PathVariable Integer index) {
        return postService.deletePost(currentPage, index);
    }

    /**
     * 用户取消发送帖子
     */
    @ApiOperation(value = "用户取消发送帖子", notes = "由于需要清除用户所上传的图片，请在用户取消（退出）发送帖子时，连接此接口")
    @GetMapping("/cancel")
    public Result cancelPost() {
        return postService.cancelPost();
    }

    /**
     * 用户发送帖子
     */
    @ApiOperation(value = "用户发送帖子")
    @PostMapping("/save")
    public Result savePost(@ApiParam(name = "post", value = "传入title,content；图片另外上传") @RequestBody Post post) {
        return postService.savePost(post);
    }

    @ApiOperation(value = "删除图片", notes = "当用户在编辑帖子时，删除上传的其中一张图片")
    @DeleteMapping("/image/{index}")
    public Result deleteImage(@ApiParam(name = "index", value = "从0开始算角标") @PathVariable int index) {
        return postService.deleteImage(index);
    }

    @ApiOperation(value = "用户上传图片", notes = "用户在即将发布的该帖子下所上传的图片，最多上传四张（图片路径：）")
    @PostMapping("/image")
    public Result uploadImage(@RequestBody MultipartFile file) {
        return postService.uploadImage(file);
    }

    /**
     * 用户获取个人的帖子，根据页数使用分页查询，用于主页展示
     */
    @ApiOperation(value = "获取个人帖子", notes = "根据页数使用分页查询，用于个人主页展示；每张图片用\",\"分开，" +
            "得麻烦前端自行切割，并在图片地址前加上\"http://ip:端口号/images/post/\"")
    @GetMapping("/{currentPage}")
    public Result getPost(@ApiParam(name = "currentPage", value = "当前帖子页数") @PathVariable Integer currentPage) {
        return postService.getPost(currentPage);
    }
}
