package com.lilac.controller;

import cn.hutool.core.lang.Dict;
import com.lilac.pojo.ImSingle;
import com.lilac.pojo.Result;
import com.lilac.service.impl.ImSingleServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping( "/imSingle")
public class ImSingleController {
    @Resource
    private ImSingleServiceImpl imSingleServiceImpl;

    /**
     * 查询所有消息
     */
    @GetMapping
    public Result findByFromUsername(@RequestParam String fromUser, @RequestParam String toUser) {
        List<ImSingle> all = imSingleServiceImpl.findByUsername(fromUser, toUser);
        return Result.success(all);
    }

    /**
     * 查询未读消息数量
     *
     * @return 未读消息数量
     */
    @GetMapping("/unReadNums")
    public Result findUnReadNums(@RequestParam String toUsername) {
        Dict dict = imSingleServiceImpl.findUnreadNums(toUsername);
        if (dict == null) {
            return Result.error("No unread messages found");  // 或者返回一个默认值
        }
        return Result.success(dict);
    }


    //发送消息
    @GetMapping("/send")
    public Result send(@RequestBody ImSingle imSingle) {
        imSingleServiceImpl.add(imSingle);
        return Result.success();
    }
}